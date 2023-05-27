package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "Items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {
    private static final String OWNER_ID = "owner_id";
    private static final String REQUEST_ID = "request_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = OWNER_ID, nullable = false)
    private User owner;
    @ManyToOne
    @JoinColumn(name = REQUEST_ID)
    private ItemRequest request;
}