package com.learnspringboot.identity_user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.learnspringboot.identity_user.dto.request.UserCreationRequest;
import com.learnspringboot.identity_user.dto.request.UserUpdateRequest;
import com.learnspringboot.identity_user.dto.response.UserResponse;
import com.learnspringboot.identity_user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
