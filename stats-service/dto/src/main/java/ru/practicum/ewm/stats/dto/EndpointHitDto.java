package ru.practicum.ewm.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.stats.dto.validation.TimestampHitValidator;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import ru.practicum.ewm.stats.dto.validation.TimestampHitValidate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    @NotNull @NotBlank
    private String app;
    @NotNull @NotBlank
    private String uri;
    @NotNull @NotBlank
    private String ip;
    @TimestampHitValidate
    private String timestamp;
}
