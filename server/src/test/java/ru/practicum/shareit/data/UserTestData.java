package ru.practicum.shareit.data;

import ru.practicum.shareit.user.dto.UserClientDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserTestData {

    public static User getUser() {
        User user = new User();
        user.setId(1);
        user.setName("Test user");
        user.setEmail("test@email");
        return user;
    }

    public static User getOwner() {
        User user = new User();
        user.setId(2);
        user.setName("Test owner");
        user.setEmail("owner@email");
        return user;
    }

    public static UserDto getUserDto() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setName("Test user dto");
        user.setEmail("test-dto@email");
        return user;
    }

    public static UserClientDto getUserClientDto() {
        UserClientDto user = new UserClientDto();
        user.setId(1);
        user.setEmail("test-dto@email");
        return user;
    }

    public static UserDto getUserDtoOwner() {
        UserDto user = new UserDto();
        user.setId(2);
        user.setName("Test owner");
        user.setEmail("owner@email");
        return user;
    }

    public static UserClientDto getUserClientDtoOwner() {
        UserClientDto user = new UserClientDto();
        user.setId(2);
        user.setEmail("owner@email");
        return user;
    }

    public static UserDto getUserForMapper() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setName("Test user");
        user.setEmail("test@email");
        return user;
    }
}
