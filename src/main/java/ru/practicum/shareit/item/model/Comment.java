package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    private static final String ITEM_ID = "item_id";
    private static final String AUTHOR_ID = "author_id";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = ITEM_ID, nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = AUTHOR_ID, nullable = false)
    private User author;
    @Column(nullable = false)
    private LocalDateTime created;
}