package ru.practicum.shareit.request;

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
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.request.dto.ItemRequestClientDto;
import ru.practicum.shareit.request.dto.RequestClientInfoDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@TestPropertySource(properties = {
        "shareit-server.url=test-url",
})
@RestClientTest(RequestClient.class)
class RequestClientTest {

    private static final String API_PREFIX = "/requests";
    @Autowired
    private RequestClient requestClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Test
    void createRequest() throws JsonProcessingException {
        ItemRequestClientDto requestClientDto = ItemRequestTestData.getItemReqDto();
        String body = objectMapper.writeValueAsString(requestClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = requestClient.createRequest(1L, requestClientDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void createRequestWhenBadReq() {
        ItemRequestClientDto requestClientDto = ItemRequestTestData.getItemReqDto();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

        ResponseEntity<Object> result = requestClient.createRequest(1L, requestClientDto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void getAllRequestsForOwner() throws JsonProcessingException {
        RequestClientInfoDto requestClientDto = ItemRequestTestData.getItemReqInfoDto();
        String body = objectMapper.writeValueAsString(List.of(requestClientDto));

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = requestClient.getAllRequestsForOwner(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void getAllRequestsForOwnerWhenServerError() {
        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ResponseEntity<Object> result = requestClient.getAllRequestsForOwner(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void getAllRequests() throws JsonProcessingException {
        RequestClientInfoDto requestClientDto = ItemRequestTestData.getItemReqInfoDto();
        String body = objectMapper.writeValueAsString(List.of(requestClientDto));
        String path = String.format("/all?from=%s&size=%s", 0, 1);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = requestClient.getAllRequests(1L, 0, 1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void getAllRequestsWhenServerError() throws JsonProcessingException {
        String path = String.format("/all?from=%s&size=%s", 0, 1);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ResponseEntity<Object> result = requestClient.getAllRequests(1L, 0, 1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void getRequestById() throws JsonProcessingException {
        RequestClientInfoDto requestClientDto = ItemRequestTestData.getItemReqInfoDto();
        String body = objectMapper.writeValueAsString(requestClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = requestClient.getRequestById(1L, 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void getRequestByIdWhenNotFound() {
        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/99"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ResponseEntity<Object> result = requestClient.getRequestById(1L, 99L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }
}