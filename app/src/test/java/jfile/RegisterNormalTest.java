package jfile;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import jfile.dto.*;
import jfile.dao.*;
import jfile.model.*;
import jfile.service.RegisterNormal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; 
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RegisterNormalTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPasswordRepository userPasswordRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterNormal registerNormal;

    @Test
    void testCreateAccount() {
        AccountDTO request = new AccountDTO("testUser", "testPass");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("testPass")).thenReturn("hashedPass");

        User savedUser = new User();
        savedUser.setUid(1L);
        savedUser.setUsername("testUser");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = registerNormal.createAccount(request);

        assertNotNull(result);
        assertEquals(1L, result.getUid());
        assertEquals("testUser", result.getUsername());

        verify(userRepository).save(any(User.class));

        
        ArgumentCaptor<UserPassword> captor = ArgumentCaptor.forClass(UserPassword.class);
        verify(userPasswordRepository).save(captor.capture());
        assertEquals(savedUser, captor.getValue().getCorrespondingUser());
        assertEquals("hashedPass", captor.getValue().getHash());
    }
}
