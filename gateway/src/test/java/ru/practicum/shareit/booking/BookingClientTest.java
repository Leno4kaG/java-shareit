package ru.practicum.shareit.booking;


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
import ru.practicum.shareit.booking.dto.BookingClientDto;
import ru.practicum.shareit.booking.dto.BookingClientRequestDto;
import ru.practicum.shareit.booking.dto.BookingClientState;
import ru.practicum.shareit.data.BookingTestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@TestPropertySource(properties = {
        "shareit-server.url=test-url",
})
@RestClientTest(BookingClient.class)
class BookingClientTest {

    private static final String API_PREFIX = "/bookings";
    @Autowired
    private BookingClient bookingClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Test
    void createBooking() throws JsonProcessingException {
        BookingClientRequestDto clientRequestDto = BookingTestData.getBookinReqDto();
        String body = objectMapper.writeValueAsString(clientRequestDto);
        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));
        ResponseEntity<Object> result = bookingClient.createBooking(1L, clientRequestDto);

        assertNotNull(result);
    }

    @Test
    void createBookingWhenBadRequest() {
        BookingClientRequestDto clientRequestDto = BookingTestData.getBookinReqDto();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX))
                .andRespond(withBadRequest());

        ResponseEntity<Object> result = bookingClient.createBooking(34L, clientRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void updateBooking() throws JsonProcessingException {
        BookingClientDto bookingClientDto = BookingTestData.getBookingDto();
        String body = objectMapper.writeValueAsString(bookingClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/" + bookingClientDto.getId() + "?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));
        ResponseEntity<Object> result = bookingClient.updateBooking(1L, bookingClientDto.getId(), true);

        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void updateBookingWhenNotFoundUser() {
        BookingClientDto bookingClientDto = BookingTestData.getBookingDto();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/" + bookingClientDto.getId() + "?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        ResponseEntity<Object> result = bookingClient.updateBooking(99L, bookingClientDto.getId(), true);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    void getBooking() throws JsonProcessingException {
        BookingClientDto bookingClientDto = BookingTestData.getBookingDto();
        String body = objectMapper.writeValueAsString(bookingClientDto);

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/" + bookingClientDto.getId()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = bookingClient.getBooking(1L, bookingClientDto.getId());

        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void getBookingWhenServerError() {
        BookingClientDto bookingClientDto = BookingTestData.getBookingDto();

        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + "/" + bookingClientDto.getId()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ResponseEntity<Object> result = bookingClient.getBooking(1L, bookingClientDto.getId());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void getBookingsForCurrentUser() throws JsonProcessingException {
        BookingClientDto bookingClientDto = BookingTestData.getBookingDto();
        String body = objectMapper.writeValueAsString(List.of(bookingClientDto));
        String path = String.format("?state=%s&from=%s&size=%s", BookingClientState.ALL, 0, 1);
        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = bookingClient.getBookingsForCurrentUser(1L, BookingClientState.ALL,
                0, 1);

        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void getBookingsForCurrentUserWhenBadReq() {

        String path = String.format("?state=%s&from=%s&size=%s", BookingClientState.ALL, 0, 1);
        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

        ResponseEntity<Object> result = bookingClient.getBookingsForCurrentUser(1L, BookingClientState.ALL,
                0, 1);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void getBookingsForAllItems() throws JsonProcessingException {

        BookingClientDto bookingClientDto = BookingTestData.getBookingDto();
        String body = objectMapper.writeValueAsString(List.of(bookingClientDto));
        String path = String.format("/owner?state=%s&from=%s&size=%s", BookingClientState.ALL, 0, 1);
        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        ResponseEntity<Object> result = bookingClient.getBookingsForAllItems(1L, BookingClientState.ALL,
                0, 1);

        assertEquals(body, objectMapper.writeValueAsString(result.getBody()));
    }

    @Test
    void getBookingsForAllItemsWhenBadReq() {

        String path = String.format("/owner?state=%s&from=%s&size=%s", BookingClientState.ALL, 0, 1);
        this.mockRestServiceServer.expect(requestTo("test-url" + API_PREFIX + path))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

        ResponseEntity<Object> result = bookingClient.getBookingsForAllItems(1L, BookingClientState.ALL,
                0, 1);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}