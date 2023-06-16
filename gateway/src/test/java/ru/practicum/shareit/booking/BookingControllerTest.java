package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingClientDto;
import ru.practicum.shareit.booking.dto.BookingClientRequestDto;
import ru.practicum.shareit.booking.dto.BookingClientState;
import ru.practicum.shareit.booking.dto.BookingClientStatus;
import ru.practicum.shareit.data.BookingTestData;
import ru.practicum.shareit.exception.BookingClientException;
import ru.practicum.shareit.exception.BookingClientNotFoundException;
import ru.practicum.shareit.exception.BookingClientValidationException;
import ru.practicum.shareit.exception.ErrorClientHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingClient bookingService;

    @InjectMocks
    private BookingClientController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .setControllerAdvice(ErrorClientHandler.class)
                .build();
    }


    @Test
    void createBookingTest_when_correct_data_201_ok() throws Exception {
        BookingClientDto bookingDto = BookingTestData.getBookingDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.createBooking(anyLong(), any(BookingClientRequestDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(bookingDto));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(BookingTestData.getBookinReqDto()))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));

    }

    @Test
    void createBookingTest_when_correct_data_400_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.createBooking(anyLong(), any(BookingClientRequestDto.class)))
                .thenThrow(BookingClientValidationException.class);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(BookingTestData.getBookinReqDto()))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    void createBookingTest_when_not_found_404_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.createBooking(anyLong(), any(BookingClientRequestDto.class)))
                .thenThrow(BookingClientNotFoundException.class);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(BookingTestData.getBookinReqDto()))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void createBookingTest_when_500_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.createBooking(anyLong(), any(BookingClientRequestDto.class)))
                .thenThrow(new BookingClientException("Test error"));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(BookingTestData.getBookinReqDto()))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andDo(print());

    }

    @Test
    void updateBookingWhen_data_correct_200_ok() throws Exception {
        BookingClientDto bookingDto = BookingTestData.getBookingDto();
        bookingDto.setStatus(BookingClientStatus.APPROVED);
        Long bookingId = 1L;
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.updateBooking(anyLong(), anyLong(), any(Boolean.class)))
                .thenReturn(ResponseEntity.ok(bookingDto));

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));
    }

    @Test
    void updateBookingWhen_not_found_404_error() throws Exception {
        Long bookingId = 99L;
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.updateBooking(anyLong(), anyLong(), any(Boolean.class)))
                .thenThrow(BookingClientNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void updateBookingWhen_when_500_error() throws Exception {
        Long bookingId = 1L;
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.updateBooking(anyLong(), anyLong(), any(Boolean.class)))
                .thenThrow(BookingClientException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andDo(print());
    }

    @Test
    void getBooking_200_ok() throws Exception {
        BookingClientDto bookingDto = BookingTestData.getBookingDto();
        Long bookingId = 1L;
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(bookingDto));
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().name())));
    }

    @Test
    void getBooking_404_error() throws Exception {
        Long bookingId = 99L;
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(BookingClientNotFoundException.class);
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getBooking_500_error() throws Exception {
        Long bookingId = 99L;
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(BookingClientException.class);
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError())
                .andDo(print());
    }

    @Test
    void getBookingsForCurrentUserWhen_All_200_Ok() throws Exception {
        BookingClientDto bookingDto = BookingTestData.getBookingDto();
        BookingClientDto bookingDto1 = BookingTestData.getBookingDto();
        bookingDto1.setStatus(BookingClientStatus.APPROVED);
        List<BookingClientDto> bookingDtoList = List.of(bookingDto, bookingDto1);
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBookingsForCurrentUser(anyLong(), any(BookingClientState.class), any(), any()))
                .thenReturn(ResponseEntity.ok(bookingDtoList));
        mockMvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoList)));
    }

    @Test
    void getBookingsForCurrentUserWhen_404_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBookingsForCurrentUser(anyLong(), any(BookingClientState.class), any(), any()))
                .thenThrow(BookingClientNotFoundException.class);
        mockMvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    void getBookingsForCurrentUserWhen_arg_error_500_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "2")
                        .param("state", "error")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    void getBookingsForCurrentUserWhen_400_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBookingsForCurrentUser(anyLong(), any(BookingClientState.class), any(), any()))
                .thenThrow(BookingClientValidationException.class);
        mockMvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());

    }

    @Test
    void getBookingsForCurrentUserWhen_500_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBookingsForCurrentUser(anyLong(), any(BookingClientState.class), any(), any()))
                .thenThrow(BookingClientException.class);
        mockMvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void getBookingsForAllItems_200_Ok() throws Exception {
        BookingClientDto bookingDto = BookingTestData.getBookingDto();
        BookingClientDto bookingDto1 = BookingTestData.getBookingDto();
        bookingDto1.setStatus(BookingClientStatus.APPROVED);
        List<BookingClientDto> bookingDtoList = List.of(bookingDto, bookingDto1);
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBookingsForAllItems(anyLong(), any(BookingClientState.class), any(), any()))
                .thenReturn(ResponseEntity.ok(bookingDtoList));
        mockMvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingDtoList)));
    }

    @Test
    void getBookingsForAllItems_404_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBookingsForAllItems(anyLong(), any(BookingClientState.class), any(), any()))
                .thenThrow(BookingClientNotFoundException.class);
        mockMvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getBookingsForAllItems_400_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBookingsForAllItems(anyLong(), any(BookingClientState.class), any(), any()))
                .thenThrow(BookingClientValidationException.class);
        mockMvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "-1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void getBookingsForAllItems_500_error() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(bookingService.getBookingsForAllItems(anyLong(), any(BookingClientState.class), any(), any()))
                .thenThrow(BookingClientException.class);
        mockMvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}