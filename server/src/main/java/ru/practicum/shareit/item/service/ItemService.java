package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.create.CommentCreateDto;
import ru.practicum.shareit.item.dto.create.ItemCreateDto;
import ru.practicum.shareit.item.dto.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDtoForComment;
import ru.practicum.shareit.item.dto.update.ItemUpdateDto;

import java.util.List;

public interface ItemService {

    List<ItemResponseDto> getAllItemsByOwner(Long userId);

    ItemResponseDtoForComment getItemById(Long itemId, Long userId);

    ItemResponseDto createItem(ItemCreateDto itemDto, Long userId);

    ItemResponseDto editItem(Long itemId, ItemUpdateDto itemDto, Long userId);

    List<ItemResponseDto> searchItem(String text, Long userId);

    CommentResponseDto createComment(CommentCreateDto comment, Long authorId, Long itemId);
}
