package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> findAllByRequestorOrderByCreated(long ownerId, Pageable pageable);
}
