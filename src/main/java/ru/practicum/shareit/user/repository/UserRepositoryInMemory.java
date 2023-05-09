package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
public class UserRepositoryInMemory implements UserRepository {

    private Map<Long, User> userMap = new HashMap<>();

    private static long id = 1;

    @Override
    public User createUser(User user) {
        user.setId(id++);
        userMap.put(user.getId(), user);
        log.info("Create user with name {}", user.getName());
        return user;
    }

    @Override
    public User getUserById(long id) {
        log.info("Get user by id {}", id);
        return Optional.ofNullable(userMap.get(id)).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User updateUser(User user) {
        userMap.put(user.getId(), user);
        log.info("Update user with name {}", user.getName());
        return user;
    }

    @Override
    public void deleteUser(long id) {
        userMap.remove(id);
        log.info("Delete user by id {}", id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }
}
