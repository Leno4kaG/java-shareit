package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.dto.UserClientDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    UserClientDto toClientDto(User user);

    User fromDto(UserDto userDto);

    @Mapping(target = "name", ignore = true)
    User fromDto(UserClientDto userDto);

    List<UserDto> toDtoList(List<User> userList);
}
