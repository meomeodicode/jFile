package jfile.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jfile.service.SignIn;
import jfile.dto.SignInRequestDTO;
import jfile.dto.SignInResponseDTO;
import jfile.dto.ErrorResponseDTO;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final SignIn signInService;

    public AuthController(SignIn signIn) {
        this.signInService = signIn;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInRequestDTO request) {
        try {
            // Log the incoming request
            logger.debug("Sign in attempt - Username: {}", request.getUsername());
            
            // Attempt to sign in
            SignInResponseDTO response = signInService.signIn(request);
            
            // Log successful response
            logger.info("Sign in successful for user: {}", request.getUsername());
            logger.debug("Generated token length: {}", 
                response.getToken() != null ? response.getToken().length() : 0);
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            logger.error("Authentication failed - Username: {}, Reason: {}", 
                request.getUsername(), e.getMessage());
                
            return ResponseEntity
                .status(401)
                .body(new ErrorResponseDTO("Invalid credentials", e.getMessage()));
                
        } catch (Exception e) {
            logger.error("Sign in error - Username: {}, Error: {}", 
                request.getUsername(), e.getMessage(), e);
                
            return ResponseEntity
                .status(500)
                .body(new ErrorResponseDTO("Internal server error", e.getMessage()));
        }
    }
}