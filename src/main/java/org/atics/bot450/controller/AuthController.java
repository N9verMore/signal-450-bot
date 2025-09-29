package org.atics.bot450.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atics.bot450.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "redirect:/scheduler"; // защищённая — незалогиненных отправит на /login
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) model.addAttribute("error", "Invalid username or password");
        if (logout != null) model.addAttribute("info", "You have been logged out");
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(@RequestParam(value = "mobileNumber", required = false) String mobileNumber,
                               Model model) {
        model.addAttribute("mobileNumber", mobileNumber);
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String mobileNumber,
                           @RequestParam String password,
                           @RequestParam String password2,
                           Model model) {
        if (!password.equals(password2)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("mobileNumber", mobileNumber);
            return "register";
        }
        try {
            userService.register(mobileNumber, password);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("mobileNumber", mobileNumber);
            return "register";
        }
        return "redirect:/login?registered";
    }
}
