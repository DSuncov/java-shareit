package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestResponseDto> getAllRequestsByAuthor(Long requestorId);

    List<ItemRequestResponseDto> getAllRequestsByUser(Long userId);

    ItemRequestResponseDto getRequestsById(Long requestId, Long userId);

    ItemRequestResponseDto createRequest(ItemRequestCreateDto itemRequestDto, Long requestorId);
}
