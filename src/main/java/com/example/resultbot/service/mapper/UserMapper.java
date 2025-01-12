package com.example.resultbot.service.mapper;

import com.example.resultbot.entity.User;
import com.example.resultbot.service.dto.RegisterUserDto;
import com.example.resultbot.service.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDto, User> {
    @Mapping(source = "roleId", target = "role.id")
    User toEntity(UserDto userDto);

    @Mapping(source = "role.id", target = "roleId")
    UserDto toDto(User user);

    @Mapping(source = "roleId", target = "role.id")
    @Mapping(target = "email", source = "email")
    User toUser(RegisterUserDto input);
}
