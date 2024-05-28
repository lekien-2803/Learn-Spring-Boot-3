package com.learnspringboot.identity_user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnspringboot.identity_user.dto.request.AuthenticationRequest;
import com.learnspringboot.identity_user.dto.response.ApiResponse;
import com.learnspringboot.identity_user.dto.response.AuthenticationResponse;
import com.learnspringboot.identity_user.service.AuthenticationService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/log-in")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        boolean result = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                    .result(AuthenticationResponse.builder()
                                .authenticated(result)
                                .build())
                    .build();
    }
    
}
