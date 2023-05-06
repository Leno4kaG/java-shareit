package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryInMemory;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryInMemory repositoryInMemory;
    private final UserMapper userMapper;

    public User createUser(UserDto userDto) {
        validateEmail(userDto.getEmail(), 0);
        User user = userMapper.fromDto(userDto);
        return repositoryInMemory.createUser(user);
    }

    public User getUserById(long id) {
        return repositoryInMemory.getUserById(id);
    }

    public User updateUser(long id, UserDto userDto) {
        User user = repositoryInMemory.getUserById(id);
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null) {
            user.setName(userDto.getName());
        }
        if (email != null) {
            validateEmail(email, id);
            user.setEmail(userDto.getEmail());
        }
        return repositoryInMemory.updateUser(user);
    }

    public void deleteUserById(long id) {
        repositoryInMemory.deleteUser(id);
    }

    public List<User> getAllUsers() {
        return repositoryInMemory.getAllUsers();
    }

    private void validateEmail(String email, long id) {
        for (User user : getAllUsers()) {
            if (user.getEmail().equals(email) && user.getId() != id) {
                log.error("User with email {} уже зарегистрирован", user.getEmail());
                throw new ValidationException();
            }
        }
    }
}
