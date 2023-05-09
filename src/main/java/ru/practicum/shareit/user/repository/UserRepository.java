package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User createUser(User user);

    User getUserById(long id);

    User updateUser(User user);

    void deleteUser(long id);

    List<User> getAllUsers();
}
