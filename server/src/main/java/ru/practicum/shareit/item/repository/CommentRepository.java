package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c " +
            "FROM Comment c " +
            "JOIN c.author a " +
            "WHERE c.author.id = :userId AND c.item.id = :itemId")
    List<Comment> findCommentsByItem(Long userId, Long itemId);
}
