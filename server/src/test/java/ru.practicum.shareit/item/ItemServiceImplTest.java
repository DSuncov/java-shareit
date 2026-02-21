package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.create.CommentCreateDto;
import ru.practicum.shareit.item.dto.create.ItemCreateDto;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.dto.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDtoForComment;
import ru.practicum.shareit.item.dto.update.ItemUpdateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ItemServiceImplTest {

    @Mock
    private ItemMapper itemMapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemRequest itemRequest;
    private Item item;
    private User owner;
    private User booker;
    private Booking booking;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest(1L, "Request description", booker, Instant.now());
        item = new Item(1L, "Item № 1", "description", true, owner, itemRequest);
        owner = new User(1L, "User №1", "user_1@yandex.ru");
        booker = new User(2L, "User №2", "user_2@yandex.ru");
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, Status.WAITING);
    }

    @Test
    @DisplayName("Получение списка всех вещей")
    void getAllItemsByOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllItemsByOwner(anyLong())).thenReturn(Collections.emptyList());

        List<ItemResponseDto> result = itemService.getAllItemsByOwner(anyLong());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findAllItemsByOwner(anyLong());

        assertNotNull(result);
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    @DisplayName("Получение вещи по id")
    void getItemById_ShouldReturnItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findNameByUser(anyLong())).thenReturn("User №2");
        when(commentRepository.findCommentsByItem(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(bookingRepository.findLastBooking(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.findNextBooking(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(itemMapper.toDtoWithComments(item)).thenReturn(new ItemResponseDtoForComment());

        itemService.getItemById(1L, 1L);
        verify(itemRepository).findById(anyLong());
        verify(userRepository).findNameByUser(anyLong());
        verify(commentRepository).findCommentsByItem(anyLong(), anyLong());
        verify(bookingRepository).findLastBooking(anyLong(), anyLong());
        verify(bookingRepository).findNextBooking(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Создание вещи")
    void createItem_ShouldReturnNewItem() {
        ItemCreateDto itemCreateDto = new ItemCreateDto("Item № 1", "description", true, 1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemMapper.toEntity(itemCreateDto)).thenReturn(item);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);

        itemService.createItem(itemCreateDto, 1L);
        verify(userRepository).findById(anyLong());
        verify(itemMapper).toEntity(itemCreateDto);
        verify(itemRequestRepository).findById(anyLong());
        verify(itemRepository).save(item);
        verify(itemMapper).toDto(item);
    }

    @Test
    @DisplayName("Обновление информации о вещи")
    void editItem_ShouldReturnUpdatedItem() {
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto("NEW NAME", "NEW DESCRIPTION", false);
        Item updatedItem = new Item(1L, "NEW NAME", "NEW DESCRIPTION", false, owner, itemRequest);
        ItemResponseDto expected = itemMapper.toDto(updatedItem);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatedItem));

        ItemResponseDto result = itemService.editItem(1L, itemUpdateDto, 1L);
        verify(userRepository).findById(anyLong());
        verify(itemRepository, times(2)).findById(anyLong());
        verify(itemRepository).save(updatedItem);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Поиск вещи с по тексту")
    void searchItem_ShouldReturnItem() {
        ItemResponseDto itemResponseDto = new ItemResponseDto(1L, "Item № 1", "description", true);
        List<Item> items = new ArrayList<>();
        items.add(item);

        List<ItemResponseDto> expected = new ArrayList<>();
        expected.add(itemResponseDto);

        when(itemRepository.search(eq("desc"), anyLong())).thenReturn(items);
        when(itemMapper.toDto(item)).thenReturn(itemResponseDto);

        List<ItemResponseDto> result = itemService.searchItem("desc", 1L);
        verify(itemRepository).search(eq("desc"), anyLong());
        verify(itemMapper).toDto(item);

        assertEquals(expected, result);

        // Пустой текст
        List<ItemResponseDto> resultEmptyList = itemService.searchItem("", 1L);
        assertEquals(Collections.emptyList(), resultEmptyList);
    }

    @Test
    @DisplayName("Создание отзыва")
    void createComment_ShouldReturnNewComment() {
        CommentCreateDto commentCreateDto = new CommentCreateDto("text", 1L, 1L);
        CommentResponseDto expected = new CommentResponseDto(1L, "text", "NAME", Instant.now());
        Comment comment = new Comment(1L, "text", item, booker, Instant.now());

        when(bookingRepository.findByAuthorAndItem(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(commentMapper.toEntity(commentCreateDto, booking)).thenReturn(comment);
        when(userRepository.findNameByUser(anyLong())).thenReturn("NAME");
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment, "NAME")).thenReturn(expected);

        CommentResponseDto result = itemService.createComment(commentCreateDto, 1L, 1L);
        verify(bookingRepository).findByAuthorAndItem(anyLong(), anyLong());
        verify(commentMapper).toEntity(commentCreateDto, booking);
        verify(userRepository).findNameByUser(anyLong());
        verify(commentRepository).save(comment);

        assertEquals(expected, result);
    }
}
