package ru.practicum.shareit.data;


import ru.practicum.shareit.user.dto.UserClientDto;
import ru.practicum.shareit.user.dto.UserDto;

public class UserTestData {


    public static UserClientDto getUserClientDto() {
        UserClientDto user = new UserClientDto();
        user.setId(1);
        user.setEmail("test-dto@email");
        return user;
    }

    public static UserDto getUserDto() {
        UserDto user = new UserDto();
        user.setId(1);
        user.setName("Test user dto");
        user.setEmail("test-dto@email");
        return user;
    }

}
