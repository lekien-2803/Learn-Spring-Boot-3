package com.learnspringboot.identity_user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.learnspringboot.identity_user.dto.request.UserCreationRequest;
import com.learnspringboot.identity_user.dto.request.UserUpdateRequest;
import com.learnspringboot.identity_user.dto.response.UserResponse;
import com.learnspringboot.identity_user.entity.User;
import com.learnspringboot.identity_user.exception.AppException;
import com.learnspringboot.identity_user.exception.ErrorCode;
import com.learnspringboot.identity_user.mapper.UserMapper;
import com.learnspringboot.identity_user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    // CREATE
    public UserResponse createUser(UserCreationRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = userMapper.toUser(request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // READ
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    // UPDATE
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }
    
    public UserResponse changeStatusUser(String id, boolean status) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setStatus(status);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // DELETE
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
