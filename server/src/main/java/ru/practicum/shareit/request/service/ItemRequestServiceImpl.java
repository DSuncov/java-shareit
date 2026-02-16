package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getAllRequestsByAuthor(Long requestorId) {
        log.info("Отправляем запрос на получение списка запросов на добавление вещи для автора ...");
        log.info("Проверяем существование пользователя с id = {}", requestorId);
        userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + requestorId + " не существует."));

        log.info("Получаем список запросов ...");
        List<ItemRequest> requestsByAuthor = itemRequestRepository.findItemRequestsByAuthor(requestorId);
        List<ItemRequestResponseDto> responseDtoList = convertListOfEntitiesToDto(requestsByAuthor);
        log.info("Запрос успешно обработан ...");
        return responseDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestResponseDto> getAllRequestsByUser(Long userId) {
        log.info("Отправляем запрос на получение списка запросов на добавление вещи для случайного пользователя ...");
        log.info("Проверяем существование пользователя с id = {}", userId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует."));

        log.info("Получаем список запросов ...");
        List<ItemRequest> requestsByOtherUsers = itemRequestRepository.findItemRequestByOtherUsers(userId);
        List<ItemRequestResponseDto> responseDtoList = convertListOfEntitiesToDto(requestsByOtherUsers);
        log.info("Запрос успешно обработан ...");
        return responseDtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestResponseDto getRequestsById(Long requestId, Long userId) {
        log.info("Отправляем запрос на получение запроса по его id ...");
        log.info("Проверяем существование пользователя с id = {}", userId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует."));

        log.info("Получаем информацию о запросе ...");
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + " отсутствует."));
        ItemRequestResponseDto itemRequestResponseDto = requestMapper.toDto(request, itemRepository.findItemsForRequest(requestId));
        log.info("Запрос успешно обработан ...");
        return itemRequestResponseDto;
    }

    @Override
    @Transactional
    public ItemRequestResponseDto createRequest(ItemRequestCreateDto itemRequestDto, Long requestorId) {
        log.info("Обрабатываем запрос на создание запроса на добавление вещи ...");
        log.info("Проверяем существование пользователя с id = {}", requestorId);
        User requestor = userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + requestorId + " не существует."));

        log.info("Получаем информацию о запросе ...");
        ItemRequest newItemRequest = requestMapper.toEntity(itemRequestDto);
        newItemRequest.setRequestor(requestor);
        ItemRequest createdRequest = itemRequestRepository.save(newItemRequest);
        log.info("Запрос успешно обработан ...");
        return requestMapper.toDto(createdRequest);
    }

    private List<ItemRequestResponseDto> convertListOfEntitiesToDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(r -> requestMapper.toDto(r, itemRepository.findItemsForRequest(r.getId())))
                .toList();
    }
}
