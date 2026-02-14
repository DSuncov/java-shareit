package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateDto {
    @NotBlank(message = "Название должно быть задано.")
    private String name;
    @NotBlank(message = "Описание должно быть указано.")
    private String description;
    @NotNull(message = "Доступность товара для бронирования должна быть указана.")
    private Boolean available;
}

