package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exception.EmailConflictException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

@Repository
public class InMemoryUserDaoImpl implements UserDao {
    public static final String USER_NOT_FOUND_MESSAGE = "User not found. id: ";
    public static final String EMAIL_CONFLICT_MESSAGE = "email is already in use by another user: ";

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long currentId = 1;

    @Override
    public User createUser(User user) {
        checkEmailIsAlreadyUsed(user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE + userId);
        }
        User oldEntry = users.get(userId);

        if (user.getName() != null) {
            oldEntry.setName(user.getName());
        }

        String oldEmail = users.get(userId).getEmail();
        String newEmail = user.getEmail();
        if (newEmail != null && !oldEmail.equals(newEmail)) {
            tryRefreshUserEmail(oldEmail, newEmail);
            oldEntry.setEmail(newEmail);
        }
        return oldEntry;
    }

    @Override
    public User findUserById(long userId) {
        if (!users.containsKey(userId)) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE + userId);
        return users.get(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        if (!users.containsKey(userId)) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE + userId);
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void checkEmailIsAlreadyUsed(String email) {
        if (emails.contains(email)) throw new EmailConflictException(EMAIL_CONFLICT_MESSAGE + email);
    }

    private void tryRefreshUserEmail(String oldEmail, String newEmail) {
        emails.remove(oldEmail);
        if (emails.contains(newEmail)) {
            emails.add(oldEmail);
            throw new EmailConflictException(EMAIL_CONFLICT_MESSAGE + newEmail);
        }
        emails.add(newEmail);
    }

    private long generateId() {
        return currentId++;
    }
}
