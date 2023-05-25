package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDB;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryDB repositoryInMemory;
    private final UserMapper userMapper;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.fromDto(userDto);
        return userMapper.toDto(repositoryInMemory.save(user));
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        return userMapper.toDto(repositoryInMemory.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Transactional
    public UserDto updateUser(long id, UserDto userDto) {
        User user = repositoryInMemory.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            user.setEmail(email);
        }
        return userMapper.toDto(repositoryInMemory.save(user));
    }

    @Transactional
    public void deleteUserById(long id) {
        repositoryInMemory.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        repositoryInMemory.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userMapper.toDtoList(repositoryInMemory.findAll());
    }

}
