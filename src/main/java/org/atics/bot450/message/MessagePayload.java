package org.atics.bot450.message;

import lombok.Data;

import java.util.List;

@Data
public class MessagePayload {
    private final String message;
    private final String number;
    private final List<String> recipients;

    public MessagePayload(String message, String number, List<String> recipients) {
        this.message = message;
        this.number = number;
        this.recipients = recipients;
    }
}
