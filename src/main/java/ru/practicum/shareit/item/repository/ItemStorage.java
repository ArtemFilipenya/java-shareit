package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    List<Item> findAllByRequest_IdIs(Long requestId);

    List<Item> findAllByOwner_IdIs(Long ownerId);

    List<Item> findAllByOwner_IdIs(Long ownerId, Pageable pageable);

    @Query("SELECT item FROM Item item " +
            "WHERE item.available = TRUE " +
            "AND (UPPER(item.name) LIKE UPPER(concat('%', ?1, '%')) " +
            "OR UPPER(item.description) LIKE UPPER(concat('%', ?1, '%')))")
    List<Item> getAllText(String text, Pageable pageable);

    @Query("SELECT item FROM Item item " +
            "WHERE item.available = TRUE " +
            "AND (UPPER(item.name) LIKE UPPER(concat('%', ?1, '%')) " +
            "OR UPPER(item.description) LIKE UPPER(concat('%', ?1, '%')))")
    List<Item> getAllText(String text);

    List<Item> findAllByRequestIn(List<ItemRequest> requests);
}