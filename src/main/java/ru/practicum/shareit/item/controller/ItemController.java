package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId) {
        var listOfItems = itemService.getAllItemsByOwner(userId);
        return ResponseEntity.ok(listOfItems);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDtoForComment> getItemById(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id вещи должно быть задано") Long itemId,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId) {
        var item = itemService.getItemById(itemId, userId);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId) {
        var listOfSearchItems = itemService.searchItem(text, userId);
        return ResponseEntity.ok(listOfSearchItems);
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(
            @NotNull(message = "В качестве Item передан null.") @Valid @RequestBody ItemCreateDto item,
            @NotNull @Positive(message = "id должно быть положительным числом") @RequestHeader("X-Sharer-User-Id") Long userId) {
        var newItem = itemService.createItem(item, userId);
        return ResponseEntity.ok(newItem);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(
            @NotNull(message = "В качестве Comment передан null.") @Valid @RequestBody CommentCreateDto comment,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long authorId,
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id вещи должно быть задано") Long itemId
            ) {
        var newComment = itemService.createComment(comment, authorId, itemId);
        return ResponseEntity.ok(newComment);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> editItem(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id вещи должно быть задано") Long itemId,
            @NotNull @Positive(message = "id должно быть положительным числом") @RequestHeader("X-Sharer-User-Id") Long userId,
            @NotNull @Valid @RequestBody ItemUpdateDto item) {
        var editItem = itemService.editItem(itemId, item, userId);
        return ResponseEntity.ok(editItem);
    }
}
