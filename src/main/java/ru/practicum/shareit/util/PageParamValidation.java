package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.PageParamException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для проверки параметров страницы
 * @param <T>
 */
@Slf4j
@Service
public class PageParamValidation<T> {


    public List<T> getListWithPageParam(Integer from, Integer size, List<T> list) {
        if (from != null && from < 0) {
            log.error("From < 0 ={}", from);
            throw new PageParamException("Индекс 1 страницы меньше 0");
        }
        if (size != null && size <= 0) {
            log.error("Size < or = 0 = {}", size);
            throw new PageParamException("Количество страниц не должно быть = 0 или быть меньше 0");
        }

        if (from == null) {
            if (size == null) {
                return list;
            }
            return list.stream().limit(size).collect(Collectors.toList());
        }
        if (from > list.size()) {
            return Collections.emptyList();
        } else
            return list.stream().skip(from).limit(size).collect(Collectors.toList());
    }
}
