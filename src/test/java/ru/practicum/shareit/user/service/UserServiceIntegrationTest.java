package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {

    @Autowired
    UserService userService;

    @Test
    void createUser() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        assertEquals(UserTestData.getUserDto().getName(), userDto.getName());
    }

    @Test
    void getUserByIdTest() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto result = userService.getUserById(userDto.getId());
        assertEquals(result, userDto);
    }

    @Test
    void updateUserTest() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDtoUpd = new UserDto();
        userDtoUpd.setName("Update");
        userDtoUpd.setEmail("update@email");
        UserDto userDto1 = userService.updateUser(userDto.getId(), userDtoUpd);
        assertEquals(userDtoUpd.getName(), userDto1.getName());
    }
}
