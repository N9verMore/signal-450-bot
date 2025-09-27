package org.atics.bot450.controller;

import lombok.RequiredArgsConstructor;
import org.atics.bot450.exception.UserAlreadyExistsException;
import org.atics.bot450.service.KeycloakUserService;
import org.atics.bot450.service.QrService;
import org.atics.bot450.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String register(@RequestParam String mobileNumber,
                           @RequestParam String password,
                           Model model,
                           RedirectAttributes ra) {
        try {
            keycloakUserService.registerUser(mobileNumber, password);
            ra.addAttribute("mobileNumber", mobileNumber);
            return "redirect:/qr-page";
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("error", "User already exists");
            model.addAttribute("mobileNumber", mobileNumber);
            return "register";
        }
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
