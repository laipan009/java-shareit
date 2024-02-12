package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "comments")
@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "text")
    private String text;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Item item;

    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User author;
}