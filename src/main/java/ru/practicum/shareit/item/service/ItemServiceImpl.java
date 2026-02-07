package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.mappers.CommentMapper;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.mappers.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserResponseDtoForBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAllItemsByOwner(Long userId) {
        log.info("Обрабатываем запрос на получение списка всех вещей владельца ...");
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует."));
        List<Item> listOfItemsByOwner = itemRepository.findAllItemsByOwner(userId);
        log.info("Запрос обработан успешно. Список вещей отправлен владельцу.");
        return listOfItemsByOwner.stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDtoForComment getItemById(Long itemId, Long userId) {
        log.info("Обрабатываем запрос на получение информации о вещи с id = {} ...", itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещи с id = " + itemId + " не существует."));
        String authorName = userRepository.findNameByUser(userId);

        log.info("Обрабатываем запрос на получение списка отзывов ... ");
        List<CommentResponseDto> listOfCommentsByItem = commentRepository.findCommentsByItem(userId, itemId)
                .stream()
                .map(c -> commentMapper.toDto(c, authorName))
                .toList();
        log.info("Обрабатываем запрос на получение последнего бронирования ... ");
        Optional<Booking> lastBooking = bookingRepository.findLastBooking(itemId, userId);
        log.info("Обрабатываем запрос на получение следующего бронирования ... ");
        Optional<Booking> nextBooking = bookingRepository.findNextBooking(itemId, userId);

        ItemResponseDtoForComment dto = itemMapper.toDtoWithComments(item);
        lastBooking.ifPresent(b -> dto.setLastBooking(convertBookingFromEntityToDto(b)));
        nextBooking.ifPresent(b -> dto.setNextBooking(convertBookingFromEntityToDto(b)));

        dto.setComments(listOfCommentsByItem);

        log.info("Запрос успешно обработан. Информация о вещи с id = {} отправлена клиенту", itemId);
        return dto;
    }

    @Override
    @Transactional
    public ItemResponseDto createItem(ItemCreateDto item, Long userId) {
        log.info("Обрабатываем запрос на добавление вещи ...");
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует."));
        Item newItem = itemMapper.toEntity(item);
        newItem.setOwner(user);
        Item createdItem = itemRepository.save(newItem);
        log.info("Запрос успешно обработан. Новая вещь сохранена в БД.");
        return itemMapper.toDto(createdItem);
    }

    @Override
    @Transactional
    public ItemResponseDto editItem(Long itemId, ItemUpdateDto item, Long userId) {
        log.info("Обрабатываем запрос на обновление информации о вещи с id = {} ...", itemId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует."));
        Optional<Item> editItem = itemRepository.findById(itemId);
        editItem.ifPresentOrElse(i -> {
            if (!i.getOwner().getId().equals(userId)) {
                throw new NotFoundException("Пользователя с id = " + userId + " не является владельцем вещи с id = " + itemId + ".");
            }

            if (item.getName() != null) {
                i.setName(item.getName());
            }

            if (item.getDescription() != null) {
                i.setDescription(item.getDescription());
            }

            if (item.getAvailable() != null) {
                i.setAvailable(item.getAvailable());
            }

        }, () -> {
            throw new NotFoundException("Вещи с id = " + item + " не существует.");
        });

        log.info("Запрос успешно обработан. Информация о вещи с id = {} обновлена.", itemId);
        return itemMapper.toDto(itemRepository.findById(itemId).get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> searchItem(String text, Long userId) {
        log.info("Обрабатываем запрос на поиск доступных вещей ...");
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> listOfItems = itemRepository.search(text, userId);
        log.info("Запрос успешно обработан. Список доступных вещей отправлен клиенту.");
        return listOfItems.stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(CommentCreateDto comment, Long authorId, Long itemId) {
        log.info("Обрабатываем запрос на создание отзыва ...");
        Booking booking = bookingRepository.findByAuthorAndItem(authorId, itemId)
                .orElseThrow(() -> new NotAvailableException("Пользователь с id = " + authorId + " не создавал бронирование либо срок бронирования еще не истек."));

        comment.setItem(itemId);
        comment.setAuthor(authorId);

        Comment commentToEntity = commentMapper.toEntity(comment, booking);
        String authorName = userRepository.findNameByUser(authorId);

        Comment newComment = commentRepository.save(commentToEntity);
        log.info("Отзыв успешно создан.");
        return commentMapper.toDto(newComment, authorName);
    }

    private BookingResponseDto convertBookingFromEntityToDto(Booking booking) {
        return bookingMapper.toDto(booking,
                new UserResponseDtoForBooking(booking.getBooker().getId()),
                new ItemResponseDtoForBooking(booking.getItem().getId(), booking.getItem().getName()));
    }
}
