package org.atics.bot450.controller;

import lombok.RequiredArgsConstructor;
import org.atics.bot450.message.GroupPayload;
import org.atics.bot450.model.Task;
import org.atics.bot450.service.SignalMessageSender;
import org.atics.bot450.service.TaskService;
import org.atics.bot450.service.QrService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BotController {

    private final TaskService taskService;
    private final SignalMessageSender signal;
    private final QrService qrService;

    @GetMapping("/")
    public String home() {
        return "redirect:/scheduler";
    }

    @GetMapping("/scheduler")
    public String schedulerPage(Authentication auth, Model model) {
        String user = auth != null ? auth.getName() : null;
        if (user == null) return "redirect:/login";

        // Если не залинкован — кидаем на страницу привязки
        if (!qrService.isAuthorized(user)) {
            return "redirect:/link-account";
        }

        List<GroupPayload> groups = signal.listGroupsFor(user);
        model.addAttribute("groups", groups);
        return "scheduler";
    }

    @PostMapping("/schedule")
    public String schedule(@RequestParam String groupId,
                           @RequestParam String message,
                           @RequestParam List<String> times,
                           Authentication authentication) {
        String owner = authentication != null ? authentication.getName() : "unknown";
        taskService.schedule(groupId, true, message, times, owner);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks")
    public String tasks(Authentication auth, Model model) {
        String owner = auth != null ? auth.getName() : null;
        if (owner == null) return "redirect:/login";
        List<Task> list = taskService.getUserTasks(owner);
        model.addAttribute("tasks", list);
        return "tasks";
    }

    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id, Authentication auth) {
        String owner = auth != null ? auth.getName() : null;
        taskService.deleteTask(id, owner);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/{id}/update")
    public String updateTask(@PathVariable Long id,
                             @RequestParam String message,
                             @RequestParam("time") String timeIso,
                             Authentication auth) {
        String owner = auth != null ? auth.getName() : null;
        taskService.updateTask(id, owner, message, timeIso);
        return "redirect:/tasks";
    }

    @GetMapping("/whoami")
    @ResponseBody
    public String whoAmI(Authentication auth) {
        return auth == null ? "Not authenticated" : "Hello, " + auth.getName();
    }
}
