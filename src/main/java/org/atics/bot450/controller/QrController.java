package org.atics.bot450.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.atics.bot450.service.QrService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class QrController {

    private final QrService qrService;

    public QrController(QrService qrService) {
        this.qrService = qrService;
    }

    // HTML страница
    @GetMapping("/qr-page")
    public String qrPage() {
        return "qr"; // templates/qr.html
    }

    // Само изображение
    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getQr() {
        return qrService.generateQrCode();
    }

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> checkStatus(HttpServletRequest request, HttpServletResponse response) {
        return qrService.checkStatus(request, response);
    }

}

