package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    private Long id;

    @NotBlank(message = "Название должно быть задано.")
    private String name;

    @NotBlank(message = "Описание должно быть указано.")
    private String description;

    @NotNull(message = "Доступность товара для бронирования должна быть указана.")
    private Boolean available;

    private Long ownerId;

    private ItemRequest request;
}
