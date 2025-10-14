package org.atics.bot450.controller;

import org.atics.bot450.service.QrCodeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
public class QrCodeController {

    private final QrCodeService qrCodeService;

    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping(value = "/link", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrCode() {

        byte[] qrCodeImage = qrCodeService.getQrCodeLink("signal-api");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCodeImage.length);

        return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/link/view", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getQrCodePage() {

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Signal QR Code</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            justify-content: center;
                            min-height: 100vh;
                            margin: 0;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        }
                        .container {
                            background: white;
                            padding: 40px;
                            border-radius: 20px;
                            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                            text-align: center;
                        }
                        h1 {
                            color: #333;
                            margin-bottom: 10px;
                        }
                        p {
                            color: #666;
                            margin-bottom: 30px;
                        }
                        img {
                            border: 3px solid #667eea;
                            border-radius: 10px;
                            padding: 10px;
                            background: white;
                        }
                        .instructions {
                            margin-top: 30px;
                            text-align: left;
                            color: #555;
                            line-height: 1.6;
                        }
                        .instructions ol {
                            padding-left: 20px;
                        }
                        .refresh-btn {
                            margin-top: 20px;
                            padding: 10px 20px;
                            background: #667eea;
                            color: white;
                            border: none;
                            border-radius: 5px;
                            cursor: pointer;
                            font-size: 16px;
                        }
                        .refresh-btn:hover {
                            background: #764ba2;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>📱 Signal QR Code</h1>
                        <p>Скануйте цей QR код у Signal для підключення пристрою</p>
                        <img src="/api/qr/link?deviceName=%s&t=%d" alt="QR Code" />
                        <div class="instructions">
                            <strong>Інструкції:</strong>
                            <ol>
                                <li>Відкрийте Signal на вашому телефоні</li>
                                <li>Перейдіть в Налаштування → Пов'язані пристрої</li>
                                <li>Натисніть "+" або "Пов'язати новий пристрій"</li>
                                <li>Скануйте цей QR код</li>
                            </ol>
                        </div>
                        <button class="refresh-btn" onclick="location.reload()">🔄 Оновити QR код</button>
                    </div>
                </body>
                </html>
                """.formatted("signal-api", System.currentTimeMillis());

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}