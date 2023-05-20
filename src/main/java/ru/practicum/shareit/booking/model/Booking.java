package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";
    private static final String ITEM_ID = "item_id";
    private static final String BOOKER_ID = "booker_id";

    @Enumerated(STRING)
    Status status;
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(name = START_DATE, nullable = false)
    private LocalDateTime start;
    @Column(name = END_DATE, nullable = false)
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = ITEM_ID, nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = BOOKER_ID, nullable = false)
    private User booker;
}