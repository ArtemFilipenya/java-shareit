package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.errors.exception.BadParameterException;
import ru.practicum.shareit.errors.exception.ObjectNotFoundException;
import ru.practicum.shareit.errors.exception.ParameterException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::convertModelToDto)
                .collect(toList());
    }

    @Override
    @Transactional
    public UserDto update(UserDto userDto, Long id) {
        User user = userStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException("User with id= " + id + " not found"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (isValidEmailAddress(userDto.getEmail())) {
                throw new BadParameterException("Invalid email format");
            }
            user.setEmail(userDto.getEmail());
        }

        try {
            return UserMapper.convertModelToDto(userStorage.save(user));
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new ParameterException(ex.getMessage());
            }
        }
        return null;
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        checkToValid(userDto);
        return UserMapper.convertModelToDto(userStorage.save(UserMapper.convertDtoToModel(userDto)));
    }

    @Override
    public UserDto get(Long id) {
        if (id == null) {
            throw new BadParameterException("Id cannot be empty");
        }
        User user = userStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException("User with id= " + id + " not found"));

        return UserMapper.convertModelToDto(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new BadParameterException("Id cannot be empty");
        }
        userStorage.deleteById(id);
    }

    private void checkToValid(UserDto user) {
        if (user == null) {
            throw new BadParameterException("An invalid parameter was passed when creating a user");
        } else if (user.getEmail() == null) {
            throw new BadParameterException("Email cannot be empty");
        } else if (isValidEmailAddress(user.getEmail())) {
            throw new BadParameterException("Email is not valid");
        }
    }

    private boolean isValidEmailAddress(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return !email.matches(emailRegex);
    }
}