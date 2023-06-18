package ru.practicum.shareit.comment.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.data.ItemTestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentMapperIntegrationTest {

    @Autowired
    CommentMapper commentMapper;

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
