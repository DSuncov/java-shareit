package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.request.ItemRequestCreateDto;
import ru.practicum.shareit.client.ItemRequestClient;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getRequestsByAuthor(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId) {
        return itemRequestClient.getAllRequestsByAuthor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsByUser(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId) {
        return itemRequestClient.getAllRequestsByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id должно быть положительным числом") Long requestId,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId
    ) {
        return itemRequestClient.getRequestsById(requestId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") Long userId,
            @NotNull(message = "В качестве Request в запросе передан null.") @Valid @RequestBody ItemRequestCreateDto itemRequestDto
    ) {
        return itemRequestClient.createRequest(itemRequestDto, userId);
    }
}
