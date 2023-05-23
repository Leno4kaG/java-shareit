package ru.practicum.shareit.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Комментарий к товару
 */
@Data
public class CommentDto {
    /**
     * Идентификатор комментария
     */
    private Long id;
    /**
     * Текст комментария
     */
    private String text;
    /**
     * Автор комментария
     */
    private String authorName;
    /**
     * Время создания комментария
     */
    private LocalDateTime created;
}
