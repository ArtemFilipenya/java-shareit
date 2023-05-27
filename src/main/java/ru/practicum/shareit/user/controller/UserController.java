package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserDto> findAll() {
        log.info("UserController.findAll()");
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody @Validated(Update.class) UserDto user,
                          @PathVariable Long userId) {
        log.info("UserController.update(userId:{})", userId);
        return userService.update(user, userId);
    }

    @PostMapping()
    public UserDto create(@RequestBody @Validated(Create.class) UserDto user) {
        log.info("UserController.create(userDto:{})", user);
        return userService.save(user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("UserController.delete(userId:{})", userId);
        userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable @NotNull Long userId) {
        log.info("UserController.get(userId:{})", userId);
        return userService.get(userId);
    }
}