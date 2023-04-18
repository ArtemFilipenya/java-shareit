package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.DeniedAccessException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryItemDaoImpl implements ItemDao {

    private static final String ITEM_NOT_FOUND_MESSAGE = "Item not found. id: ";
    private static final String DENIED_ACCESS_MESSAGE = "The user is not the owner of the thing";

    private final Map<Long, Item> items = new HashMap<>();
    private long currentId = 1;

    @Override
    public Item createItem(Item item) {
        long id = generateId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        long itemId = item.getId();

        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(ITEM_NOT_FOUND_MESSAGE + itemId);
        }

        Item updatedItem = items.get(itemId);

        if (!updatedItem.getOwner().equals(item.getOwner())) {
            throw new DeniedAccessException(DENIED_ACCESS_MESSAGE +
                    "userId: " + item.getOwner() + ", itemId: " + itemId);
        }

        refreshItem(updatedItem, item);
        return updatedItem;
    }

    @Override
    public Item findItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException(ITEM_NOT_FOUND_MESSAGE + itemId);
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllItems(Long userId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(userId)) result.add(item);
        }
        return result;
    }

    @Override
    public List<Item> findItemsByRequest(String text) {
        List<Item> result = new ArrayList<>();
        String wantedItem = text.toLowerCase();

        for (Item item : items.values()) {
            String itemName = item.getName().toLowerCase();
            String itemDescription = item.getDescription().toLowerCase();

            if ((itemName.contains(wantedItem) || itemDescription.contains(wantedItem))
                    && item.getAvailable().equals(true)) {
                result.add(item);
            }
        }
        return result;
    }

    private long generateId() {
        return currentId++;
    }

    private void refreshItem(Item oldEntry, Item newEntry) {
        String name = newEntry.getName();
        if (name != null) {
            oldEntry.setName(name);
        }

        String description = newEntry.getDescription();
        if (description != null) {
            oldEntry.setDescription(description);
        }

        Boolean available = newEntry.getAvailable();
        if (available != null) {
            oldEntry.setAvailable(available);
        }
    }
}
