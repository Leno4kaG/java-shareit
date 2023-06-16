package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.exception.ErrorClientHandler;
import ru.practicum.shareit.exception.UserClientNotFoundException;
import ru.practicum.shareit.exception.ValidationClientException;
import ru.practicum.shareit.user.dto.UserClientDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserClient userService;

    @InjectMocks
    private UserClientController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ErrorClientHandler.class)
                .build();
    }


    @Test
    void createUserWhen_201_OK() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.createUser(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(userDto));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void createUserWhen_409_error() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.createUser(any()))
                .thenThrow(ValidationClientException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    void createUserWhen_404_OK() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.createUser(any()))
                .thenThrow(UserClientNotFoundException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void createUserWhen_500_OK() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.createUser(any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void getUserByIdWhen_200_OK() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.getUserById(anyLong()))
                .thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUserByIdWhen_404_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.getUserById(anyLong()))
                .thenThrow(UserClientNotFoundException.class);

        mockMvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getUserByIdWhen_500_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.getUserById(anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/users/{id}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void updateUserWhen_200_OK() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.updateUser(anyLong(), any()))
                .thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(patch("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUserWhen_404_error() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.updateUser(anyLong(), any()))
                .thenThrow(UserClientNotFoundException.class);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void updateUserWhen_500_error() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.updateUser(anyLong(), any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(patch("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void deleteUserWhen_200_OK() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void deleteUserWhen_404_OK() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        doThrow(UserClientNotFoundException.class).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void deleteUserWhen_500_OK() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        doThrow(RuntimeException.class).when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{id}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void getAllUsersWhen_200_OK() throws Exception {
        UserClientDto userDto = UserTestData.getUserDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.getAllUsers())
                .thenReturn(ResponseEntity.ok(List.of(userDto)));

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$.[0].email", is(userDto.getEmail())));
    }

    @Test
    void getAllUsersWhen_500_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(userService.getAllUsers())
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}