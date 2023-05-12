package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        return UserMapper.convertFromUserToDto(userRepository.save(UserMapper.convertFromDtoToUser(userDto)));
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserDto userDto) {
        User foundedUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id= %s not found", userId)));

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            foundedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            foundedUser.setEmail(userDto.getEmail());
        }
        return UserMapper.convertFromUserToDto(foundedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id= %s not found", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }
}