package com.learnspringboot.identity_user.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 3, message = "username must be at least 3 characters.")
    String username;

    @Email
    String email;

    @Size(min = 8, message = "password must be at least 8 character")
    String password;
    
    String firstName;
    String lastName;
    LocalDate dob;    
    boolean status;
}
