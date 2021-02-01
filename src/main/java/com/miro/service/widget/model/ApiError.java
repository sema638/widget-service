package com.miro.service.widget.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private final LocalDateTime timestamp;
    private final String message;
    private final String details;
}
