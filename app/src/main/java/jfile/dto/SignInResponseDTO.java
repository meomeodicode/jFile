package jfile.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class SignInResponseDTO {
    private String token;
    private String username;
    private Long userId;
}