package org.atics.bot450.service;

import lombok.extern.slf4j.Slf4j;
import org.atics.bot450.message.GroupPayload;
import org.atics.bot450.message.MessagePayload;
import org.atics.bot450.model.TaskEntity;
import org.atics.bot450.model.TaskStatus;
import org.atics.bot450.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SignalMessageSender {

    private final RestTemplate restTemplate;
    private final TaskRepository tasks;
    private final QrService qrService;

    private final String apiUrl;

    public SignalMessageSender(RestTemplate restTemplate,
                               TaskRepository tasks,
                               QrService qrService,
                               @Value("${org.atics.signal.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.tasks = tasks;
        this.qrService = qrService;
        this.apiUrl = apiUrl;
    }

    public List<GroupPayload> listGroupsFor(String phoneNumber) {
        if (!qrService.isAuthorized(phoneNumber)) {
            return List.of();
        }
        return fetchGroups(phoneNumber);
    }

    public void sendToGroup(String senderNumber, String groupId, String message) {
        String url = apiUrl + "/v2/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        MessagePayload payload = new MessagePayload(message, senderNumber, List.of(groupId));
        HttpEntity<MessagePayload> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity(url, request, String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Signal send failed: " + resp.getStatusCode() + " / " + resp.getBody());
        }
    }

    @Scheduled(fixedDelay = 15_000)
    public void dispatchDueTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<TaskEntity> due = tasks.findTop100ByStatusAndScheduledAtBeforeOrderByScheduledAtAsc(TaskStatus.PENDING, now);
        if (due.isEmpty()) return;

        Map<String, List<TaskEntity>> byOwner = due.stream().collect(Collectors.groupingBy(TaskEntity::getOwnerUsername));
        byOwner.forEach((owner, ownerTasks) -> {
            boolean linked = qrService.isAuthorized(owner);
            if (!linked) {
                // помечаем как FAILED, чтобы не зацикливаться
                ownerTasks.forEach(t -> t.setStatus(TaskStatus.FAILED));
                tasks.saveAll(ownerTasks);
                log.warn("Owner {} not linked to Signal. {} task(s) marked FAILED.", owner, ownerTasks.size());
                return;
            }

            for (TaskEntity t : ownerTasks) {
                try {
                    sendToGroup(owner, t.getGroupId(), t.getMessage());
                    t.setStatus(TaskStatus.SENT);
                    t.setSentAt(LocalDateTime.now());
                    tasks.save(t);
                    log.info("Task {} sent to group {} by {}", t.getId(), t.getGroupId(), owner);
                } catch (Exception ex) {
                    t.setStatus(TaskStatus.FAILED);
                    tasks.save(t);
                    log.error("Task {} failed: {}", t.getId(), ex.getMessage(), ex);
                }
            }
        });
    }

    private List<GroupPayload> fetchGroups(String number) {
        String url = apiUrl + "/v1/groups/" + number;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<GroupPayload[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                GroupPayload[].class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to fetch groups: " + response.getStatusCode());
        }
        return Arrays.asList(response.getBody());
    }

    public Optional<String> resolveGroupIdByName(String number, String groupName) {
        return listGroupsFor(number).stream()
                .filter(g -> Objects.equals(g.getName(), groupName))
                .map(GroupPayload::getId)
                .findFirst();
    }
}
