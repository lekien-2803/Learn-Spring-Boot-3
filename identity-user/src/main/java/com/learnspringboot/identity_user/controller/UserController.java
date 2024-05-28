package com.learnspringboot.identity_user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnspringboot.identity_user.dto.request.UserCreationRequest;
import com.learnspringboot.identity_user.dto.request.UserUpdateRequest;
import com.learnspringboot.identity_user.dto.response.ApiResponse;
import com.learnspringboot.identity_user.dto.response.UserResponse;
import com.learnspringboot.identity_user.entity.User;
import com.learnspringboot.identity_user.service.UserService;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    // CREATE
    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request ) {
        UserResponse user = userService.createUser(request);
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);
        
        return apiResponse;
    }
    
    // READ
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable String userId) {
        UserResponse user = userService.getUserById(userId);
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);

        return apiResponse;
    }
    
    // UPDATE
    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        UserResponse user = userService.updateUser(userId, request);
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);

        return apiResponse;
    }

    @PatchMapping("/{userId}")
    public ApiResponse<UserResponse> changeUserStatus(@PathVariable String userId, @RequestParam("status") boolean status) {
        UserResponse user = userService.changeStatusUser(userId, status);

        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);

        return apiResponse;
    }

    // DELETE
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        ApiResponse<String> apiResponse = new ApiResponse<>();

        apiResponse.setMessage("User has been deleted.");

        return apiResponse;
    }
}
