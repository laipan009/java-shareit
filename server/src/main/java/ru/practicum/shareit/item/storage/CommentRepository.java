package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @EntityGraph(attributePaths = {"item", "author", "item.owner"})
    List<Comment> findCommentsByItem_Id(Integer itemId);

    @EntityGraph(attributePaths = {"item", "author", "item.owner"})
    List<Comment> findByItem_Owner_Id(int userId);
}