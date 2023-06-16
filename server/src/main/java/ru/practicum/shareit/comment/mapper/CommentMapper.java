package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment fromDto(CommentDto commentDto);


    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toDto(Comment comment);

    List<CommentDto> toListDto(List<Comment> comments);
}
