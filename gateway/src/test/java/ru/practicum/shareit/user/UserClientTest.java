package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.user.dto.UserClientDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@TestPropertySource(properties = {
        "shareit-server.url=test-url",
})
@RestClientTest(UserClient.class)
public class UserClientTest {

    private static final String API_PREFIX = "/users";
    @Autowired
    private UserClient userClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Test
    public void createUser() throws JsonProcessingException {
        UserClientDto userClientDto = UserTestData.getUserDto();
        String body = objectMapper.writeValueAsString(userClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = userClient.createUser(userClientDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    public void createUserWhenBadReq() {
        UserClientDto userClientDto = UserTestData.getUserDto();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

        ResponseEntity<Object> result = userClient.createUser(userClientDto);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getUserById() throws JsonProcessingException {
        UserClientDto userClientDto = UserTestData.getUserDto();
        String body = objectMapper.writeValueAsString(userClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = userClient.getUserById(1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    public void getUserByIdWhenNotFound() {

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/99"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ResponseEntity<Object> result = userClient.getUserById(99L);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void updateUser() throws JsonProcessingException {
        UserClientDto userClientDto = UserTestData.getUserDto();
        String body = objectMapper.writeValueAsString(userClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = userClient.updateUser(1L, userClientDto);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    public void updateUserWhenNotFound() {
        UserClientDto userClientDto = UserTestData.getUserDto();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/99"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ResponseEntity<Object> result = userClient.updateUser(99L, userClientDto);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void deleteUser() {

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        userClient.deleteUser(1L);
        mockRestServiceServer.verify();
    }

    @Test
    public void getAllUsers() throws JsonProcessingException {
        UserClientDto userClientDto = UserTestData.getUserDto();
        String body = objectMapper.writeValueAsString(List.of(userClientDto));

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = userClient.getAllUsers();
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    public void getAllUsersWhenServerError() throws JsonProcessingException {
        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ResponseEntity<Object> result = userClient.getAllUsers();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}
