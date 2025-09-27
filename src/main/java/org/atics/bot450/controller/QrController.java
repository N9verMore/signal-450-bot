package org.atics.bot450.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.atics.bot450.service.KeycloakUserService;
import org.atics.bot450.service.QrService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class QrController {

    private final QrService qrService;
    private final KeycloakUserService keycloakUserService;

    public QrController(QrService qrService, KeycloakUserService keycloakUserService) {
        this.qrService = qrService;
        this.keycloakUserService = keycloakUserService;
    }

    @GetMapping("/qr-page")
    public String qrPage() {
        return "qr"; // templates/qr.html
    }

    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getQr() {
        return qrService.generateQrCode();
    }

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> checkStatus(HttpServletRequest request, HttpServletResponse response, String mobileNumber) {
        return keycloakUserService.checkStatus(request, response, mobileNumber);
    }

}

