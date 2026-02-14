package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;

import java.util.List;

public interface ItemService {

    List<ItemResponseDto> getAllItemsByOwner(Long userId);

    ItemResponseDtoForComment getItemById(Long itemId, Long userId);

    ItemResponseDto createItem(ItemCreateDto itemDto, Long userId);

    ItemResponseDto editItem(Long itemId, ItemUpdateDto itemDto, Long userId);

    List<ItemResponseDto> searchItem(String text, Long userId);

    CommentResponseDto createComment(CommentCreateDto comment, Long authorId, Long itemId);
}
