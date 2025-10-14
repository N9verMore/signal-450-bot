package org.atics.bot450.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QrCodeService {

    private final RestTemplate restTemplate;
    private final String apiUrl;

    public QrCodeService(RestTemplate restTemplate,
                         @Value("${org.atics.signal.api.url}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
    }

    public byte[] getQrCodeLink(String deviceName) {
        String url = apiUrl + "/v1/qrcodelink?device_name=" + deviceName;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.IMAGE_PNG));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch QR code: " + response.getStatusCode());
        }
    }
}
