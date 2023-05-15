package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "items", schema = "public")
public class Item {
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_NAME = "item_name";
    private static final String DESCRIPTION = "description";
    private static final String AVAILABLE = "available";
    private static final String OWNER_ID = "owner_id";
    private static final int MAX_NAME_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ITEM_ID, nullable = false)
    private long id;
    @Column(name = ITEM_NAME, length = MAX_NAME_LENGTH, nullable = false)
    private String name;
    @Column(name = DESCRIPTION, nullable = false)
    private String description;
    @Column(name = AVAILABLE, nullable = false)
    private Boolean available;
    @ManyToOne
    @JoinColumn(name = OWNER_ID)
    @ToString.Exclude
    private User owner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && Objects.equals(name, item.name) && Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }
}