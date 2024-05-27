package com.learnspringboot.identity_user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.learnspringboot.identity_user.dto.request.UserCreationRequest;
import com.learnspringboot.identity_user.dto.request.UserUpdateRequest;
import com.learnspringboot.identity_user.entity.User;
import com.learnspringboot.identity_user.exception.AppException;
import com.learnspringboot.identity_user.exception.ErrorCode;
import com.learnspringboot.identity_user.repository.UserRepository;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // CREATE
    public User createUser(UserCreationRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(request.getPassword())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .dob(request.getDob())
            .status(request.isStatus())
            .build();

        return userRepository.save(user);
    }

    // READ
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    // UPDATE
    public User updateUser(String id, UserUpdateRequest request) {
        User user = getUserById(id);

        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }
    
    public User changeStatusUser(String id, boolean status) {
        User user = getUserById(id);

        user.setStatus(status);

        return userRepository.save(user);
    }

    // DELETE
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
