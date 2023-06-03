package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.user.dto.UserRequestDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Object> findUser(@PathVariable("id") Long id) {
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(Create.class) UserRequestDto user) {
        return userClient.saveUser(user);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Object> update(@RequestBody @Validated(Update.class) UserRequestDto user, @PathVariable("id") Long id) {
        return userClient.update(user, id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id) {
        return userClient.deleteUser(id);
    }
}
