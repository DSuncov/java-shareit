package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
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
    public ResponseEntity<List<ItemDto>> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        var listOfItems = itemService.getAllItemsByOwner(userId);
        return ResponseEntity.ok(listOfItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(
            @PathVariable("id")
            @Positive(message = "id должно быть положительным числом")
            @NotNull(message = "id вещи должно быть задано") Long itemId) {
        var item = itemService.getItemById(itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        var listOfSearchItems = itemService.searchItem(text);
        return ResponseEntity.ok(listOfSearchItems);
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(
            @NotNull(message = "В качестве Item передан null.") @Valid @RequestBody Item item,
            @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        var newItem = itemService.createItem(item, userId);
        return ResponseEntity.ok(newItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> editItem(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id вещи должно быть задано") Long itemId,
            @NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
            @NotNull @RequestBody Item item) {
        var editItem = itemService.editItem(itemId, item, userId);
        return ResponseEntity.ok(editItem);
    }
}
