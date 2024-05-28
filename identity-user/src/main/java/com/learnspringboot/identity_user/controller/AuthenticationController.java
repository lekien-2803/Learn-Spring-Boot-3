package com.learnspringboot.identity_user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnspringboot.identity_user.dto.request.AuthenticationRequest;
import com.learnspringboot.identity_user.dto.request.IntrospectRequest;
import com.learnspringboot.identity_user.dto.response.ApiResponse;
import com.learnspringboot.identity_user.dto.response.AuthenticationResponse;
import com.learnspringboot.identity_user.dto.response.IntrospectResponse;
import com.learnspringboot.identity_user.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {

        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                    .result(authenticationResponse)
                    .build();
    }
    
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
        IntrospectResponse introspectResponse = authenticationService.introspect(request);
        
        return ApiResponse.<IntrospectResponse>builder()
                            .result(introspectResponse)
                            .build();
    }
    
}
