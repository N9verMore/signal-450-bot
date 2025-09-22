package org.atics.bot450.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private Long id;
    private String groupName;
    private String message;
    private List<String> times;
    private String createdBy;
}
