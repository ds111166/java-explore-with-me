package ru.practicum.ewm.stats.server.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String status;
    private String reason;
    private String message;
    private String timestamp;
}
