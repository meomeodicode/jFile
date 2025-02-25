package jfile.service;

import jfile.dao.UserPasswordRepository;
import jfile.dao.UserRepository;
import jfile.dto.AccountDTO;
import jfile.dto.UserDTO;
import jfile.model.User;
import jfile.model.UserPassword;

import javax.management.RuntimeErrorException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@Service
public class RegisterNormal implements Account {
    private final UserRepository newUser;
    private final UserPasswordRepository passwordRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegisterNormal(UserRepository user, UserPasswordRepository passwordRepository, BCryptPasswordEncoder passwordEncoder)
    {
        this.newUser = user;
        this.passwordRepository = passwordRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public UserDTO createAccount(AccountDTO registrationRequest)
    {
    try {
        if (newUser.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new RuntimeErrorException(null);  
        }
        
        String hashedPassword = passwordEncoder.encode(registrationRequest.getPassword());

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        User savedUser = newUser.save(user);

        UserPassword userPassword = new UserPassword();
        userPassword.setCorrespondingUser(savedUser);
        userPassword.setHash(hashedPassword);
        passwordRepository.save(userPassword);
        
        return new UserDTO(savedUser.getUid(), savedUser.getUsername());

    } catch (CannotGetJdbcConnectionException e) {
        System.err.println("Database connection error: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Database connection error detected. See logs for details.", e);
    }
   }
}
