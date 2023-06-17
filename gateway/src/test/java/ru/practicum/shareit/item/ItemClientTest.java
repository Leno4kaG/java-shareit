package ru.practicum.shareit.item;


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
import ru.practicum.shareit.comment.dto.CommentClientDto;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.item.dto.ItemClientWithBooking;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@TestPropertySource(properties = {
        "shareit-server.url=test-url",
})
@RestClientTest(ItemClient.class)
class ItemClientTest {

    private static final String API_PREFIX = "/items";
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Test
    void createItem() throws JsonProcessingException {
        ItemDto itemClientDto = ItemTestData.getItemDto();
        String body = objectMapper.writeValueAsString(itemClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = itemClient.createItem(1L, itemClientDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void createItemWhenUserNotFound() {
        ItemDto itemClientDto = ItemTestData.getItemDto();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ResponseEntity<Object> result = itemClient.createItem(1L, itemClientDto);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void updateItemDto() throws JsonProcessingException {
        ItemDto itemClientDto = ItemTestData.getItemDto();
        String body = objectMapper.writeValueAsString(itemClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + "/" + itemClientDto.getId()))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = itemClient.updateItemDto(1L, itemClientDto, 1L);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void updateItemDtoWhenBadReq() {
        ItemDto itemClientDto = ItemTestData.getItemDto();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + "/" + itemClientDto.getId()))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withBadRequest());

        ResponseEntity<Object> result = itemClient.updateItemDto(1L, itemClientDto, 1L);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void getItem() throws JsonProcessingException {
        ItemClientWithBooking withBooking = ItemTestData.getItemWithBooking();
        String body = objectMapper.writeValueAsString(withBooking);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + "/" + withBooking.getId()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = itemClient.getItem(1L, 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void getItemWhenNotFound() {
        ItemClientWithBooking withBooking = ItemTestData.getItemWithBooking();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + "/99"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ResponseEntity<Object> result = itemClient.getItem(99L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void getAllItems() throws JsonProcessingException {
        ItemClientWithBooking withBooking = ItemTestData.getItemWithBooking();
        String body = objectMapper.writeValueAsString(List.of(withBooking));
        String path = String.format("?from=%s&size=%s", 0, 1);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = itemClient.getAllItems(1L, 0, 1);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void getAllItemsWhenServerError() {
        String path = String.format("?from=%s&size=%s", 0, 1);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ResponseEntity<Object> result = itemClient.getAllItems(1L, 0, 1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void searchItems() throws JsonProcessingException {
        ItemDto itemClientDto = ItemTestData.getItemDto();
        String body = objectMapper.writeValueAsString(List.of(itemClientDto));
        String path = String.format("/search?from=%s&size=%s&text=%s", 0, 1, "test");

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = itemClient.searchItems(0, 1, "test", 1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void searchItemsWhenServerError() {
        String path = String.format("/search?from=%s&size=%s&text=%s", 0, 1, "test");

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ResponseEntity<Object> result = itemClient.searchItems(0, 1, "test", 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void addComment() throws JsonProcessingException {
        CommentClientDto commentClientDto = ItemTestData.getCommentDto();
        String body = objectMapper.writeValueAsString(commentClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + "/1" + "/comment"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = itemClient.addComment(1L, 1L, commentClientDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void addCommentWhenBadReq() {
        CommentClientDto commentClientDto = ItemTestData.getCommentDto();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX
                        + "/1" + "/comment"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

        ResponseEntity<Object> result = itemClient.addComment(1L, 1L, commentClientDto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}