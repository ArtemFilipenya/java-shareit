package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    private static final String REQUEST_ID = "request_id";
    private static final String DESCRIPTION = "description";
    private static final String REQUESTER_ID = "requester_id";
    private static final String REQUEST_TIME = "request_time";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = REQUEST_ID)
    private long id;

    @Column(name = DESCRIPTION)
    private String description;

    @Column(name = REQUESTER_ID)
    private long creatorId;

    @Column(name = REQUEST_TIME)
    private LocalDateTime requestTime;
}