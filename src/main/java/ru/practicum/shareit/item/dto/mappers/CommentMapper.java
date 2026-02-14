package ru.practicum.shareit.item.dto.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {
    public CommentResponseDto toDto(Comment comment, String authorName) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(authorName);
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public Comment toEntity(CommentCreateDto dto, Booking booking) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());
        return comment;
    }
}
