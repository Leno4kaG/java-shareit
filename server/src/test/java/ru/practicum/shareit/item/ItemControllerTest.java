package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }


    @Test
    void createItemWhen_data_correct_201_ok() throws Exception {
        ItemDto itemDto = ItemTestData.getItemDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemService.createItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void createItemWhen_404_error() throws Exception {
        ItemDto itemDto = ItemTestData.getItemDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemService.createItem(any(ItemDto.class), anyLong()))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void createItemWhen_500_error() throws Exception {
        ItemDto itemDto = ItemTestData.getItemDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemService.createItem(any(ItemDto.class), anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void updateItemDtoWhen_data_correct_200_ok() throws Exception {
        ItemDto itemDto = ItemTestData.getItemDto();
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemService.updateItem(any(ItemDto.class), anyLong()))
                .thenReturn(itemWithBooking);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithBooking.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.comments[0].id", is(itemWithBooking.getComments().get(0).getId()), Long.class));
    }

    @Test
    void updateItemDtoWhen_404_error() throws Exception {
        ItemDto itemDto = ItemTestData.getItemDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemService.updateItem(any(ItemDto.class), anyLong()))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void updateItemDtoWhen_500_error() throws Exception {
        ItemDto itemDto = ItemTestData.getItemDto();
        objectMapper.registerModule(new JavaTimeModule());
        when(itemService.updateItem(any(ItemDto.class), anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void getItemWhen_200_OK() throws Exception {

        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemWithBooking);

        mockMvc.perform(get("/items/{itemId}", itemWithBooking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(itemWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemWithBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemWithBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithBooking.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.comments[0].id", is(itemWithBooking.getComments().get(0).getId()), Long.class));
    }

    @Test
    void getItemWhen_404_error() throws Exception {
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        when(itemService.getItem(anyLong(), anyLong()))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", itemWithBooking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getItemWhen_500_error() throws Exception {
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        when(itemService.getItem(anyLong(), anyLong()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/items/{itemId}", itemWithBooking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void getAllItemsWhen_200_Ok() throws Exception {
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        when(itemService.getAllItems(anyLong(), any(), any()))
                .thenReturn(List.of(itemWithBooking));

        mockMvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(itemWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemWithBooking.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemWithBooking.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemWithBooking.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.[0].comments[0].id", is(itemWithBooking.getComments().get(0).getId()), Long.class));
    }

    @Test
    void getAllItemsWhen_404_error() throws Exception {
        when(itemService.getAllItems(anyLong(), any(), any()))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void getAllItemsWhen_500_error() throws Exception {
        when(itemService.getAllItems(anyLong(), any(), any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "2")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void searchItemsWhen_200_OK() throws Exception {
        ItemDto itemDto = ItemTestData.getItemDto();
        when(itemService.searchItems(anyString(), anyLong(), any(), any()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "2")
                        .param("text", "test")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable()), Boolean.class));
    }

    @Test
    void searchItemsWhenTextNull_200_OK() throws Exception {

        mockMvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "2")
                        .param("text", "")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void searchItemsWhen_404_OK() throws Exception {
        when(itemService.searchItems(anyString(), anyLong(), any(), any()))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "2")
                        .param("text", "test")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void searchItemsWhen_500_OK() throws Exception {
        when(itemService.searchItems(anyString(), anyLong(), any(), any()))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(get("/items/search")
                        .param("from", "0")
                        .param("size", "2")
                        .param("text", "test")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Test
    void addCommentWhen_200_OK() throws Exception {
        CommentDto commentDto = ItemTestData.getCommentDto();
        objectMapper.registerModule(new JavaTimeModule());
        Long itemId = 1L;
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void addCommentWhen_400_ERROR() throws Exception {
        CommentDto commentDto = ItemTestData.getCommentDto();
        objectMapper.registerModule(new JavaTimeModule());
        Long itemId = 1L;
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(CommentValidationException.class);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void addCommentWhen_404_error() throws Exception {
        CommentDto commentDto = ItemTestData.getCommentDto();
        objectMapper.registerModule(new JavaTimeModule());
        Long itemId = 1L;
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(ItemNotFoundException.class);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void addCommentWhen_500_error() throws Exception {
        CommentDto commentDto = ItemTestData.getCommentDto();
        objectMapper.registerModule(new JavaTimeModule());
        Long itemId = 1L;
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(RuntimeException.class);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());

    }
}