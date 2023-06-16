package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserClientDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserClientController {

    private final UserClient userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserClientDto user) {
        log.info("create user {}", user.getEmail());
        ResponseEntity<Object> objectResponseEntity = userService.createUser(user);
        log.info("RESP {}", objectResponseEntity);
        return objectResponseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable long id, @RequestBody UserClientDto userClientDto) {
        return userService.updateUser(id, userClientDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userService.getAllUsers();
    }
}
