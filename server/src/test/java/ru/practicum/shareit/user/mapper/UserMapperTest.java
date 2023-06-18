package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.MapperTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    UserMapper userMapper = MapperTestData.getUserMapper();

    @Test
    void toDto() {
        User user = UserTestData.getUser();
        UserDto userDto = UserTestData.getUserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        UserDto result = userMapper.toDto(user);

        assertEquals(userDto, result);
    }

    @Test
    void toDtoWhenNull() {
        UserDto result = userMapper.toDto(null);

        assertNull(result);
    }

    @Test
    void fromDto() {
        User user = UserTestData.getUser();
        UserDto userDto = UserTestData.getUserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        User result = userMapper.fromDto(userDto);

        assertEquals(user, result);
    }

    @Test
    void fromDtoWhenNull() {

        User result = userMapper.fromDto((UserDto) null);

        assertNull(result);
    }

    @Test
    void toDtoList() {
        User user = UserTestData.getUser();
        UserDto userDto = UserTestData.getUserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        List<UserDto> list = userMapper.toDtoList(List.of(user));

        assertEquals(List.of(userDto), list);
    }

    @Test
    void toDtoListWhenNull() {

        List<UserDto> list = userMapper.toDtoList(null);

        assertNull(list);
    }
}