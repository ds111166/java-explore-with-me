package ru.practicum.ewm.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.stats.dto.validation.TimestampHitValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    @NotBlank
    @NotNull
    private String app;
    @NotBlank
    @NotNull
    private String uri;
    @NotBlank
    @NotNull
    private String ip;
    @TimestampHitValidate
    private String timestamp;
}
