package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }


    @Test
    void createRequestWhen_200_OK() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.createRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void createRequestWhen_404_OK() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.createRequest(anyLong(), any(ItemRequestDto.class)))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void createRequestWhen_500_OK() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.createRequest(anyLong(), any(ItemRequestDto.class)))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void getAllRequestsForOwnerWhen_200_OK() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequestsForOwner(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getAllRequestsForOwnerWhen_404_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequestsForOwner(anyLong()))
                .thenThrow(ItemRequestNotFoundException.class);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getAllRequestsForOwnerWhen_500_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequestsForOwner(anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void getAllRequestsWhen_200_OK() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequests(anyLong(), any(), any()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getAllRequestsWhen_404_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequests(anyLong(), any(), any()))
                .thenThrow(ItemRequestNotFoundException.class);

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getAllRequestsWhen_500_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequests(anyLong(), any(), any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void getRequestByIdWhen_200_OK() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.requestorId", is(itemRequestDto.getRequestorId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getRequestByIdWhen_404_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(ItemRequestNotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getRequestByIdWhen_500_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}