package ru.practicum.shareit.request;

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
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.exception.ErrorClientHandler;
import ru.practicum.shareit.exception.ItemClientNotFoundException;
import ru.practicum.shareit.exception.ItemRequestClientNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestClientDto;
import ru.practicum.shareit.request.dto.RequestClientInfoDto;

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
    private RequestClient itemRequestService;

    @InjectMocks
    private RequestController itemRequestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .setControllerAdvice(ErrorClientHandler.class)
                .build();
    }


    @Test
    void createRequestWhen_201_OK() throws Exception {
        RequestClientInfoDto itemRequestDto = ItemRequestTestData.getItemReqInfoDto();
        ItemRequestClientDto body = ItemRequestTestData.getItemReqDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.createRequest(anyLong(), any(ItemRequestClientDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(body));

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(body))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void createRequestWhen_404_OK() throws Exception {

        ItemRequestClientDto body = ItemRequestTestData.getItemReqDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.createRequest(anyLong(), any(ItemRequestClientDto.class)))
                .thenThrow(ItemClientNotFoundException.class);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(body))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void createRequestWhen_500_OK() throws Exception {
        ItemRequestClientDto body = ItemRequestTestData.getItemReqDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.createRequest(anyLong(), any(ItemRequestClientDto.class)))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(body))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void getAllRequestsForOwnerWhen_200_OK() throws Exception {
        RequestClientInfoDto itemRequestDto = ItemRequestTestData.getItemReqInfoDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequestsForOwner(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(itemRequestDto)));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getAllRequestsForOwnerWhen_404_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequestsForOwner(anyLong()))
                .thenThrow(ItemRequestClientNotFoundException.class);

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
        RequestClientInfoDto itemRequestDto = ItemRequestTestData.getItemReqInfoDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequests(anyLong(), any(), any()))
                .thenReturn(ResponseEntity.ok(List.of(itemRequestDto)));

        mockMvc.perform(get("/requests/all")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getAllRequestsWhen_param_null_200_OK() throws Exception {

        objectMapper.registerModule(new JavaTimeModule());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getAllRequestsWhen_404_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getAllRequests(anyLong(), any(), any()))
                .thenThrow(ItemRequestClientNotFoundException.class);

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
        RequestClientInfoDto itemRequestDto = ItemRequestTestData.getItemReqInfoDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemRequestDto));

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getRequestByIdWhen_404_OK() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenThrow(ItemRequestClientNotFoundException.class);

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
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}