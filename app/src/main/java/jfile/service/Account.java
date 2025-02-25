package jfile.service;

import org.springframework.http.ResponseEntity;

import jfile.dto.AccountDTO;
import jfile.dto.UserDTO;

public interface Account {
    UserDTO createAccount(AccountDTO registrationRequest);

}
