package org.atics.bot450.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.atics.bot450.service.QrService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class QrController {

    private final QrService qrService;

    @GetMapping("/qr-page")
    public String qrPage() {
        return "qr";
    }

    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getQr() {
        return qrService.generateQrCode();
    }

    @GetMapping("/status")
    @ResponseBody
    @Cacheable("qr-status")
    public Map<String, Object> checkStatus(HttpServletRequest request,
                                           HttpServletResponse response,
                                           @RequestParam String mobileNumber) {
        boolean authorized = qrService.isAuthorized(mobileNumber);
        if (authorized) {
            request.getSession(true).setAttribute("LINKED", true);
        }
        return Map.of("authorized", authorized);
    }
}
