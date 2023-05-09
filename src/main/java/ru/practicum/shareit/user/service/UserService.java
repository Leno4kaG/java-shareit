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

    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto.getEmail(), 0);
        User user = userMapper.fromDto(userDto);
        return userMapper.toDto(repositoryInMemory.createUser(user));
    }

    public UserDto getUserById(long id) {
        return userMapper.toDto(repositoryInMemory.getUserById(id));
    }

    public UserDto updateUser(long id, UserDto userDto) {
        User user = repositoryInMemory.getUserById(id);
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            validateEmail(email, id);
            user.setEmail(email);
        }
        return userMapper.toDto(repositoryInMemory.updateUser(user));
    }

    public void deleteUserById(long id) {
        repositoryInMemory.deleteUser(id);
    }

    public List<UserDto> getAllUsers() {
        return userMapper.toDtoList(repositoryInMemory.getAllUsers());
    }

    private void validateEmail(String email, long id) {
        for (UserDto user : getAllUsers()) {
            if (user.getEmail().equals(email) && user.getId() != id) {
                log.error("User with email {} уже зарегистрирован", user.getEmail());
                throw new ValidationException();
            }
        }
    }
}
