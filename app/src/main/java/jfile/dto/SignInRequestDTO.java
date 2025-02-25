package jfile.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class SignInRequestDTO {
    @NotBlank(message = "Username cannot be empty")
    private String username;
    
    @NotBlank(message = "Password cannot be empty")
    private String password;
}