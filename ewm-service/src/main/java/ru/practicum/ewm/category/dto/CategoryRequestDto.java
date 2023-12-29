package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDto {
    @NotNull(groups = Marker.OnCreate.class, message = "не должно равняться null")
    @Max(groups = Marker.OnCreate.class, value = 50)
    @Min(groups = Marker.OnCreate.class, value = 1)
    private String name;    //Наименование категории
}
