package jfile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jfile.dto.AccountDTO;
import jfile.dto.UserDTO;
import jfile.service.Account;

@RestController
@RequestMapping("/register")
public class RegisterController {
    private final Account userAccount;
    
    public RegisterController(Account userAccount) {
        this.userAccount = userAccount;
    }

    @PostMapping
    public ResponseEntity<UserDTO> register(@RequestBody AccountDTO registrationRequest) {
        try {
            UserDTO userDTO = userAccount.createAccount(registrationRequest);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}
