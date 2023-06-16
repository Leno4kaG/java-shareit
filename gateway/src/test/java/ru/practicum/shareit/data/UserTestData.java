package ru.practicum.shareit.data;


import ru.practicum.shareit.user.dto.UserClientDto;

public class UserTestData {



    public static UserClientDto getUserDto() {
        UserClientDto user = new UserClientDto();
        user.setId(1);
        user.setName("Test user dto");
        user.setEmail("test-dto@email");
        return user;
    }

}
