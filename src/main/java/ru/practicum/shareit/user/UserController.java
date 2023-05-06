package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.OnCreate;
import ru.practicum.shareit.exception.OnUpdate;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static ru.practicum.shareit.user.UserMapper.toUserDto;
import static ru.practicum.shareit.user.UserMapper.toUserDtoList;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Validated(OnCreate.class) @RequestBody UserDto userDto) {
        log.info("Received POST request for new User.");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @Validated(OnUpdate.class) @RequestBody UserDto userDto) {
        log.info("Received PUT request for User ID = {}", id);
        return userService.update(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable long id) {
        log.info("Received GET request for User ID = {}", id);
        return toUserDto(userService.getById(id));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Received GET request for all Users");
        return toUserDtoList(userService.getAll());
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        log.info("Received DELETE request for User ID = {}", id);
        userService.deleteById(id);
    }

    @DeleteMapping
    public void deleteAll() {
        log.info("Received DELETE request for all Users");
        userService.deleteAll();
    }
}