package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository = mock(UserRepository.class);
    private UserMapper userMapper = mock(UserMapper.class);

    private UserService userService = new UserService(userRepository, userMapper);

    @Test
    void createUserTest() {
        User user = UserTestData.getUser();
        UserDto userDto = UserTestData.getUserDto();

        when(userMapper.fromDto(any(UserDto.class))).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(userDto);

        UserDto result = userService.createUser(userDto);

        assertEquals(userDto, result);
    }


    @Test
    void getUserByIdTest() {
        User user = UserTestData.getUser();
        UserDto userDto = UserTestData.getUserDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDto(any())).thenReturn(userDto);

        UserDto result = userService.getUserById(user.getId());

        assertEquals(userDto, result);
    }

    @Test
    void getUserByIdTestError() {
        User user = UserTestData.getUser();
        UserDto userDto = UserTestData.getUserDto();

        when(userRepository.findById(anyLong())).thenThrow(new UserNotFoundException(user.getId()));
        when(userMapper.toDto(any())).thenReturn(userDto);

        UserNotFoundException result = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(user.getId()));

        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void updateUserTest() {
        User user = UserTestData.getUser();
        UserDto userDto = UserTestData.getUserDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(userDto);

        UserDto result = userService.updateUser(user.getId(), userDto);

        assertEquals(userDto, result);
    }

    @Test
    void updateUserTestError() {
        User user = UserTestData.getUser();
        UserDto userDto = UserTestData.getUserDto();

        when(userRepository.findById(anyLong())).thenThrow(new UserNotFoundException(user.getId()));


        UserNotFoundException result = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(user.getId(), userDto));

        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    void deleteUserByIdTest() {
        User user = UserTestData.getUser();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(any());

        userService.deleteUserById(user.getId());

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void getAllUsersTest() {
        User user = UserTestData.getUser();
        UserDto userDto = UserTestData.getUserDto();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDtoList(anyList())).thenReturn(List.of(userDto));

        List<UserDto> result = userService.getAllUsers();

        assertEquals(userDto, result.get(0));
    }
}