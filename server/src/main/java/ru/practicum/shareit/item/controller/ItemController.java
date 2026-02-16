package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.create.CommentCreateDto;
import ru.practicum.shareit.item.dto.create.ItemCreateDto;
import ru.practicum.shareit.item.dto.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDtoForComment;
import ru.practicum.shareit.item.dto.update.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getItemsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        var listOfItems = itemService.getAllItemsByOwner(userId);
        return ResponseEntity.ok(listOfItems);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDtoForComment> getItemById(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        var item = itemService.getItemById(itemId, userId);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        var listOfSearchItems = itemService.searchItem(text, userId);
        return ResponseEntity.ok(listOfSearchItems);
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(
            @RequestBody ItemCreateDto item,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        var newItem = itemService.createItem(item, userId);
        return ResponseEntity.ok(newItem);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentResponseDto> createComment(
            @RequestBody CommentCreateDto comment,
            @RequestHeader("X-Sharer-User-Id") Long authorId,
            @PathVariable Long itemId
            ) {
        var newComment = itemService.createComment(comment, authorId, itemId);
        return ResponseEntity.ok(newComment);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> editItem(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemUpdateDto item) {
        var editItem = itemService.editItem(itemId, item, userId);
        return ResponseEntity.ok(editItem);
    }
}
