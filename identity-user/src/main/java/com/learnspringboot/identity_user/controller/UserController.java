package com.learnspringboot.identity_user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnspringboot.identity_user.dto.request.UserCreationRequest;
import com.learnspringboot.identity_user.dto.request.UserUpdateRequest;
import com.learnspringboot.identity_user.dto.response.ApiResponse;
import com.learnspringboot.identity_user.entity.User;
import com.learnspringboot.identity_user.service.UserService;

import jakarta.validation.Valid;

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
public class UserController {
    @Autowired
    private UserService userService;

    // CREATE
    @PostMapping
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request ) {
        User user = userService.createUser(request);
        ApiResponse apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);
        
        return apiResponse;
    }
    
    // READ
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ApiResponse<User> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        ApiResponse apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);

        return apiResponse;
    }
    
    // UPDATE
    @PutMapping("/{userId}")
    public ApiResponse<User> updateUser(@PathVariable String userId, @RequestBody @Valid UserUpdateRequest request) {
        User user = userService.updateUser(userId, request);
        ApiResponse apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);

        return apiResponse;
    }

    @PatchMapping("/{userId}")
    public ApiResponse<User> changeUserStatus(@PathVariable String userId, @RequestParam("status") boolean status) {
        User user = userService.changeStatusUser(userId, status);

        ApiResponse apiResponse = new ApiResponse<>();

        apiResponse.setResult(user);

        return apiResponse;
    }

    // DELETE
    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        ApiResponse apiResponse = new ApiResponse<>();

        apiResponse.setMessage("User has been deleted.");

        return apiResponse;
    }
}
