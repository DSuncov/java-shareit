package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @GetMapping
    public ResponseEntity<List<ItemRequestResponseDto>> getRequestsByAuthor(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        var listOfRequestsByRequestor = itemRequestService.getAllRequestsByAuthor(userId);
        return ResponseEntity.ok(listOfRequestsByRequestor);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestResponseDto>> getRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        var listOfRequestsByUser = itemRequestService.getAllRequestsByUser(userId);
        return ResponseEntity.ok(listOfRequestsByUser);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestResponseDto> getRequestById(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        var itemRequest = itemRequestService.getRequestsById(requestId, userId);
        return ResponseEntity.ok(itemRequest);
    }

    @PostMapping
    public ResponseEntity<ItemRequestResponseDto> createRequest(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestCreateDto itemRequestDto
    ) {
        var itemRequest = itemRequestService.createRequest(itemRequestDto, userId);
        return ResponseEntity.ok(itemRequest);
    }
}
