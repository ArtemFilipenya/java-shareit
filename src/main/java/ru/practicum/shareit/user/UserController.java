package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation_markers.Create;
import ru.practicum.shareit.validation_markers.Update;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable long id) {
        log.info("UserController.getUserById() id={}", id);
        return UserMapper.convertFromUserToDto(userService.getById(id));
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Received GET request for all Users");
        return UserMapper.convertUsersToDtoList(userService.findAll());
    }

    @PostMapping
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("UserController.create() userId={}", userDto.getId());
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("UserController.update() id={}", id);
        return userService.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable long id) {
        log.info("UserController.deleteUserById() id={}", id);
        userService.deleteById(id);
    }
}