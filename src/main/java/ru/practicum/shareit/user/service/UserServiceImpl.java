package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserDao;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private UserDao userDao;

    @Override
    public User createUser(User user) {
        log.info("Create new user: {}", user.getName());
        return userDao.createUser(user);
    }

    @Override
    public User updateUser(long userId, User user) {
        log.info("Update user with id: {}", userId);
        return userDao.updateUser(userId, user);
    }

    @Override
    public User findUserById(long userId) {
        log.info("Find user with id: {}", userId);
        return userDao.findUserById(userId);
    }

    @Override
    public void deleteUserById(long userId) {
        log.info("Delete user with id: {}", userId);
        userDao.deleteUserById(userId);
    }

    @Override
    public List<User> findAllUsers() {
        log.info("Get all users.");
        return userDao.findAllUsers();
    }
}