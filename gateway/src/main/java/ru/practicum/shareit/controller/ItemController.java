package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.item.CommentCreateDto;
import ru.practicum.shareit.dto.item.ItemCreateDto;
import ru.practicum.shareit.dto.item.ItemUpdateDto;
import ru.practicum.shareit.client.ItemClient;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId) {
        return itemClient.getAllItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id вещи должно быть задано") Long itemId,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId) {
        return itemClient.searchItem(text, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(
            @NotNull(message = "В качестве Item передан null.") @Valid @RequestBody ItemCreateDto item,
            @NotNull @Positive(message = "id должно быть положительным числом") @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.createItem(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @NotNull(message = "В качестве Comment передан null.") @Valid @RequestBody CommentCreateDto comment,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long authorId,
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id вещи должно быть задано") Long itemId
            ) {
        return itemClient.createComment(comment, authorId, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id вещи должно быть задано") Long itemId,
            @NotNull @Positive(message = "id должно быть положительным числом") @RequestHeader("X-Sharer-User-Id") Long userId,
            @NotNull @Valid @RequestBody ItemUpdateDto item) {
        return itemClient.editItem(itemId, item, userId);
    }
}
