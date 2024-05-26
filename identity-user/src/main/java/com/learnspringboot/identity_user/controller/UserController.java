package com.learnspringboot.identity_user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnspringboot.identity_user.dto.request.UserCreationRequest;
import com.learnspringboot.identity_user.dto.request.UserUpdateRequest;
import com.learnspringboot.identity_user.entity.User;
import com.learnspringboot.identity_user.service.UserService;
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
    public ResponseEntity<User> createUser(@RequestBody UserCreationRequest request ) {
        User user = userService.createUser(request);
        
        return new ResponseEntity<User>(user, HttpStatus.CREATED);
    }
    
    // READ
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = userService.getUserById(userId);

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
    
    // UPDATE
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        User user = userService.updateUser(userId, request);
        
        return new ResponseEntity<User>(user, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<User> changeUserStatus(@PathVariable String userId, @RequestParam("status") boolean status) {
        User user = userService.changeStatusUser(userId, status);

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    // DELETE
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);

        return new ResponseEntity<>(HttpStatus.RESET_CONTENT);
    }
}
