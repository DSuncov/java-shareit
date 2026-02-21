package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.create.CommentCreateDto;
import ru.practicum.shareit.item.mappers.CommentMapper;
import ru.practicum.shareit.item.dto.response.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentMapperTest {

    private final CommentMapper commentMapper = new CommentMapper();

    private final User author = new User(1L, "User № 1", "user_1@yandex.ru");
    private final User owner = new User(2L, "User № 2", "user_2@yandex.ru");

    private final Item item = new Item(1L, "Item № 1 name", "Item № 1 description", true, owner, null);
    private final Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, author, Status.WAITING);

    @Test
    @DisplayName("Маппинг из Entity в DTO")
    void toDto_ShouldReturnCommentResponseDto() {
        Comment comment = new Comment(1L, "text", item, author, Instant.now());

        CommentResponseDto dto = commentMapper.toDto(comment, author.getName());

        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getItem(), item);
        assertEquals(comment.getAuthor(), author);
    }

    @Test
    @DisplayName("Маппинг из DTO в Entity")
    void toEntity_ShouldReturnComment() {
        CommentCreateDto dto = new CommentCreateDto("text", 1L, 1L);

        Comment comment = commentMapper.toEntity(dto, booking);

        assertNotNull(comment);
        assertEquals(dto.getText(), comment.getText());
        assertEquals(comment.getItem(), item);
        assertEquals(comment.getAuthor(), author);
        assertEquals(comment.getItem().getId(), dto.getItem());
        assertEquals(comment.getAuthor().getId(), dto.getAuthor());



    }

}
