package org.example.bankcardmanagement.mapper;

import org.example.bankcardmanagement.dto.UserDtoRequest;
import org.example.bankcardmanagement.dto.UserDtoResponse;
import org.example.bankcardmanagement.model.Role;
import org.example.bankcardmanagement.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "userRoles")
    UserDtoResponse toUserDtoResponse(User user);

    User toUser(UserDtoRequest userDtoRequest);

    @Named("userRoles")
    default Set<String> transferUserRoles(Set<Role> roles) throws Exception {
        return roles.stream().map(Role::getRoleName).collect(Collectors.toSet());
    }
}
