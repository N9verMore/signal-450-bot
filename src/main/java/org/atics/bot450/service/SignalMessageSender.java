package org.atics.bot450.service;

import lombok.Data;
import org.atics.bot450.message.GroupPayload;
import org.atics.bot450.message.MessagePayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Data
public class SignalMessageSender {
    private final String cronTime;
    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String groupName;
    private final String senderNumbersString;
    private final String stringMessage;

    public SignalMessageSender(RestTemplate restTemplate, @Value("${org.atics.bot450.cron.time}") String cronTime,
                               @Value("${org.atics.signal.api.url}") String apiUrl,
                               @Value("${org.atics.signal.group.name}") String groupName,
                               @Value("${org.atics.signal.sender.number}") String senderNumbersString,
                               @Value("${org.atics.bot450.message}") String stringMessage) {
        this.cronTime = cronTime;
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.groupName = groupName;
        this.senderNumbersString = senderNumbersString;
        this.stringMessage = stringMessage;
    }

    @Scheduled(cron = "${org.atics.bot450.cron.time}")
    public void startMessageBroadcast(){
        List<String> numbers = parseSenderNumbers();
        for(String number : numbers){
            sendScheduledMessage(number);
        }
    }

    private void sendScheduledMessage(String number) {
        System.out.println("📩 Відправка доповіді у " + java.time.LocalDateTime.now());
        String url = apiUrl + "/v2/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        List<String> groupId = List.of(Optional.of(getGroupId(groupName, number))
                .orElseThrow(() -> new IllegalArgumentException("Group id with name " + groupName + " haven`t found")));

        var payload = new MessagePayload(stringMessage, number, groupId);

        HttpEntity<MessagePayload> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity(url, request, String.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send message: " + resp.getStatusCode() + " / " + resp.getBody());
        } else {
            System.out.println("📩 Доповідь успішно відправлена о: " + java.time.LocalDateTime.now());
        }
    }

    private String getGroupId(String groupName, String number) {
        String url = apiUrl + "/v1/groups/" + number;
        List<GroupPayload> groupPayloadList;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<GroupPayload[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                GroupPayload[].class
        );
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            groupPayloadList = Arrays.asList(response.getBody());
        } else {
            throw new RuntimeException("Failed to fetch groups: " + response.getStatusCode());
        }
        return groupPayloadList.stream().filter(group -> group.getName().equals(groupName))
                .map(GroupPayload::getId).findFirst().orElse(null);
    }

    private List<String> parseSenderNumbers() {
        return Arrays.stream(this.senderNumbersString.split(",")).toList();
    }
}
