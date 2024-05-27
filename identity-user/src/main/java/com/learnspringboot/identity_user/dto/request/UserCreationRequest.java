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
    @Size(min = 3, message = "INVALID_USERNAME")
    String username;
    
    @Email(message = "INVALID_EMAIL")
    String email;

    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;
    
    String firstName;
    String lastName;
    LocalDate dob;    
    boolean status;
}
