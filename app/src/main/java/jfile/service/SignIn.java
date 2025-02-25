package jfile.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;

import jfile.dao.UserRepository;
import jfile.dao.UserPasswordRepository;
import jfile.utility.JWTTokenProvider;
import jfile.model.User;
import jfile.model.UserPassword;
import jfile.dto.SignInRequestDTO;
import jfile.dto.SignInResponseDTO;

@Service
public class SignIn {
    private final UserRepository userRepository;
    private final UserPasswordRepository passwordRepository;
    private final JWTTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(SignIn.class);

    public SignIn(UserRepository userRepository, 
                 UserPasswordRepository passwordRepository,
                 JWTTokenProvider jwtTokenProvider,
                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordRepository = passwordRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public SignInResponseDTO signIn(SignInRequestDTO request) {
        logger.debug("Attempting to find user: {}", request.getUsername());
        
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> {
                logger.error("User not found: {}", request.getUsername());
                return new BadCredentialsException("Invalid username or password");
            });
        logger.debug("Found user: {}", user.getUsername());

        UserPassword userPassword = passwordRepository.findHashByUserId(user.getUid())
            .orElseThrow(() -> {
                logger.error("Password not found for user: {}", user.getUsername());
                return new BadCredentialsException("Invalid username or password");
            });
        logger.debug("Found password hash for user");

        if (!passwordEncoder.matches(request.getPassword(), userPassword.getHash())) {
            logger.error("Password mismatch for user: {}", user.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
        logger.debug("Password matched successfully");

        String token = jwtTokenProvider.generateToken(
            user.getUid().toString(),
            user.getUsername()
        );
        logger.debug("Generated JWT token of length: {}", token.length());

        return new SignInResponseDTO(token, user.getUsername(), user.getUid());
    }
}