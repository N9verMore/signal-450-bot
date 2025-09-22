package org.atics.bot450.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class QrService {

    private final RestTemplate restTemplate;
    private final String signalApiUrl;
    private final SecurityContextRepository securityContextRepository;
    @Setter
    @Getter
    private String registeredNumber;
    private final String DEVICE_NAME = "signal-api";

    public QrService(@Value("${org.atics.signal.api.url}") String signalApiUrl, SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
        this.restTemplate = new RestTemplate();
        this.signalApiUrl = signalApiUrl;
    }

    public byte[] generateQrCode() {
        String url = signalApiUrl + "/v1/qrcodelink?device_name=" + DEVICE_NAME;
        ResponseEntity<byte[]> response =
                restTemplate.getForEntity(url, byte[].class);
        return response.getBody();
    }

    public Map<String, Object> checkStatus(HttpServletRequest request, HttpServletResponse response) {
        boolean authorized = isAuthorized();
        if (authorized) {
            UserDetails userDetails = User.withUsername(getRegisteredNumber())
                    .password("")
                    .authorities("ROLE_USER")
                    .build();

            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            securityContextRepository.saveContext(context, request, response);

            request.getSession(true)
                    .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        }
        return Map.of("authorized", authorized);
    }


    public boolean isAuthorized() {
        if (registeredNumber == null) {
            return false;
        }

        try {
            String url = signalApiUrl + "/v1/devices/" + registeredNumber;
            ResponseEntity<LinkedDevice[]> response =
                    restTemplate.getForEntity(url, LinkedDevice[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return false;
            }

            for (LinkedDevice device : response.getBody()) {
                if (DEVICE_NAME.equals(device.getName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static class LinkedDevice {
        private long creation_timestamp;
        @Getter
        private long last_seen_timestamp;
        @Getter
        private String name;

    }
}

