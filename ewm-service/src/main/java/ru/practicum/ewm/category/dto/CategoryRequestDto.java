package ru.practicum.ewm.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDto {
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(groups = Marker.OnCreate.class, min = 1, max = 50)
    private String name;    //Наименование категории
}
