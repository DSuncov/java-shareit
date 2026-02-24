package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mappers.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequestCreateDto itemRequestCreateDto;
    private ItemRequestResponseDto itemRequestResponseDto;
    private ItemRequest itemRequest;
    private User requestor;
    private User user;

    @BeforeEach
    void setUp() {
        itemRequestCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDto.setDescription("Request description");

        itemRequestResponseDto = new ItemRequestResponseDto();
        itemRequestResponseDto.setId(1L);
        itemRequestResponseDto.setDescription("Request description");
        itemRequestResponseDto.setCreated(Instant.now());
        itemRequestResponseDto.setItems(Collections.emptyList());

        requestor = new User();
        requestor.setId(1L);
        requestor.setName("User №1");
        requestor.setEmail("user_1@yandex.ru");

        user = new User();
        user.setId(2L);
        user.setName("User №2");
        user.setEmail("user_2@yandex.ru");

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request description");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(Instant.now());
    }

    @Test
    @DisplayName("Возвращает список запросов автора")
    void getAllRequestsByAuthor_ShouldReturnListOfRequests() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));

        itemRequestService.getAllRequestsByAuthor(1L);
        verify(userRepository).findById(1L);
        verify(itemRequestRepository).findItemRequestsByAuthor(1L);
    }

    @Test
    @DisplayName("Попытка получить список запросов от несуществующего пользователя")
    void getAllRequestsByAuthor_ShouldReturnThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequestsByAuthor(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Возвращает список запросов случайного пользователя")
    void getAllRequestsByUser_ShouldReturnListOfRequests() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        itemRequestService.getAllRequestsByUser(2L);
        verify(userRepository).findById(2L);
        verify(itemRequestRepository).findItemRequestByOtherUsers(2L);
    }

    @Test
    @DisplayName("Попытка получить список запросов от несуществующего пользователя")
    void getAllRequestsByUser_ShouldReturnThrowException() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequestsByUser(2L));
        verify(userRepository).findById(2L);
    }

    @Test
    @DisplayName("Возвращает информацию о запросе по id")
    void getRequestsById_ShouldReturnRequest() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findItemsForRequest(1L)).thenReturn(Collections.emptyList());
        when(requestMapper.toDto(itemRequest, Collections.emptyList())).thenReturn(itemRequestResponseDto);

        ItemRequestResponseDto result = itemRequestService.getRequestsById(1L, 2L);
        verify(userRepository).findById(2L);
        verify(itemRequestRepository).findById(1L);
        verify(itemRepository).findItemsForRequest(1L);
        verify(requestMapper).toDto(itemRequest, Collections.emptyList());

        assertEquals(itemRequestResponseDto.getDescription(), result.getDescription());
    }

    @Test
    @DisplayName("Добавление запроса")
    void createRequest_ShouldReturnNewRequest() {
        ItemRequest newItemRequest = new ItemRequest();
        newItemRequest.setDescription("Request description");

        when(userRepository.findById(1L)).thenReturn(Optional.of(requestor));
        when(requestMapper.toEntity(itemRequestCreateDto)).thenReturn(newItemRequest);
        when(itemRequestRepository.save(newItemRequest)).thenReturn(itemRequest);
        when(requestMapper.toDto(itemRequest)).thenReturn(itemRequestResponseDto);

        ItemRequestResponseDto result = itemRequestService.createRequest(itemRequestCreateDto, 1L);
        verify(userRepository).findById(1L);
        verify(requestMapper).toEntity(itemRequestCreateDto);
        verify(itemRequestRepository).save(newItemRequest);
        verify(requestMapper).toDto(itemRequest);

        assertEquals(itemRequestResponseDto.getDescription(), result.getDescription());
    }
}
