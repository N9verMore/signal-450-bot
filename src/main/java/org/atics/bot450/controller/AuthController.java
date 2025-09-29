package org.atics.bot450.controller;

import lombok.RequiredArgsConstructor;
import org.atics.bot450.message.GroupPayload;
import org.atics.bot450.service.QrService;
import org.atics.bot450.service.SignalService;
import org.atics.bot450.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final SignalService signalService;

    private final QrService qrService;

    @GetMapping("/")
    public String home(Authentication auth) {
        return (auth != null && auth.isAuthenticated())
                ? "redirect:/scheduler"
                : "login";
    }

    @GetMapping("/login")
    public String login(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/scheduler";
        }
        return "login";
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
        userService.register(mobileNumber, password);
        if(qrService.isAuthorized(mobileNumber)) {
            return "redirect:/scheduler";
        }
        return "redirect:linkAccount";
    }

    @GetMapping("/register")
    public String register(Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            return "redirect:/scheduler";
        }
        return "register";
    }

    @GetMapping("/link-account")
    public String linkAccountPage() {
        return "linkAccount";
    }
}
