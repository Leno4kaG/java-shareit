package ru.practicum.shareit.comment.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.MapperTestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentMapperTest {

    CommentMapper commentMapper = MapperTestData.getCommentMapper();

    @Test
    void fromDto() {
        CommentDto commentDto = ItemTestData.getCommentDto();
        Comment comment = ItemTestData.getComment();
        comment.setItem(null);
        comment.setAuthor(null);
        comment.setCreated(commentDto.getCreated());

        Comment result = commentMapper.fromDto(commentDto);

        assertEquals(comment, result);
    }

    @Test
    void fromDtoWhenNull() {

        Comment result = commentMapper.fromDto(null);

        assertNull(result);
    }

    @Test
    void toDto() {
        CommentDto commentDto = ItemTestData.getCommentDto();
        Comment comment = ItemTestData.getComment();
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        CommentDto result = commentMapper.toDto(comment);

        assertEquals(commentDto, result);
    }

    @Test
    void toDtoWhenNull() {

        CommentDto result = commentMapper.toDto(null);

        assertNull(result);
    }

    @Test
    void toListDto() {
        CommentDto commentDto = ItemTestData.getCommentDto();
        Comment comment = ItemTestData.getComment();
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        List<CommentDto> list = commentMapper.toListDto(List.of(comment));

        assertEquals(List.of(commentDto), list);
    }

    @Test
    void toListDtoWhenAuthorNull() {
        CommentDto commentDto = ItemTestData.getCommentDto();
        Comment comment = ItemTestData.getComment();
        comment.setAuthor(null);
        commentDto.setAuthorName(null);
        commentDto.setCreated(comment.getCreated());

        List<CommentDto> list = commentMapper.toListDto(List.of(comment));

        assertEquals(List.of(commentDto), list);
        assertNull(list.get(0).getAuthorName());
    }

    @Test
    void toListDtoWhenNull() {

        List<CommentDto> list = commentMapper.toListDto(null);

        assertNull(list);
    }
}