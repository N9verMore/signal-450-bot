package org.atics.bot450.controller;

import lombok.RequiredArgsConstructor;
import org.atics.bot450.service.KeycloakUserService;
import org.atics.bot450.service.QrService;
import org.atics.bot450.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BotController {

    private final KeycloakUserService keycloakUserService;
    private final TaskService taskService;
    private final QrService qrService;

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String phoneNumber,
                           @RequestParam String password) {
        keycloakUserService.registerUser(phoneNumber, password);
        qrService.setRegisteredNumber(phoneNumber);
        return "redirect:/qr-page?phoneNumber=" + phoneNumber;
    }

    @GetMapping("/scheduler")
    public String schedulerPage() {
        return "scheduler";
    }

    @PostMapping("/schedule")
    public String schedule(@RequestParam String groupName,
                           @RequestParam String message,
                           @RequestParam List<String> times,
                           @RequestParam String createdBy) {
        taskService.schedule(groupName, message, times, createdBy);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks")
    public String tasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "tasks";
    }

    @PostMapping("/tasks/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasks";
    }
}
