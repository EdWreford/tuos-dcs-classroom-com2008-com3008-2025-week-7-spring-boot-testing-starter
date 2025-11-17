package com.example.data_example.service;

import com.example.data_example.domain.SecurityUser;
import com.example.data_example.domain.User;
import com.example.data_example.dto.TokenDTO;
import com.example.data_example.dto.UserSignupDTO;
import com.example.data_example.exceptions.UsernameExistsException;
import com.example.data_example.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JpaUserDetailsService jpaUserDetailsService;

    @Mock 
    private TokenService tokenService;

    @InjectMocks
    private UserService classUnderTest;

    private static final String SIGNUP_USERNAME = "testuser1";
    private static final String SIGNUP_PASSWORD = "Password1!";

    @Test
    void signupNewUser_shouldThrowExceptionWhenUsernameExists() {
        UserSignupDTO userSignupDTO = new UserSignupDTO();
        userSignupDTO.setUsername(SIGNUP_USERNAME);
        userSignupDTO.setPassword(SIGNUP_PASSWORD);
        when(userRepository.existsUserByUsername(userSignupDTO.getUsername()))
            .thenReturn(true);
        UsernameExistsException ex = assertThrows(UsernameExistsException.class, () -> {
            classUnderTest.signupNewUser(userSignupDTO);
        });
        assertThat(ex.getMessage()).contains("Username already exists, please try to be original");
        verify(userRepository, never()).save(any());
        verify(tokenService, never()).generateToken(any(), anyString());
        verifyNoMoreInteractions(userRepository, tokenService, passwordEncoder, jpaUserDetailsService);
    }

    private static final String TEST_JWT_TOKEN = "test-token";
    private static final String TEST_ENCODED_PASSWORD = "test-encoded-password";
    private static final String ROLE_USER = "";

    @Test
    void signupNewUser_success() {
        UserSignupDTO userSignupDTO = new UserSignupDTO();
        userSignupDTO.setUsername(SIGNUP_USERNAME);
        userSignupDTO.setPassword(SIGNUP_PASSWORD);

        User savedUser = new User(
                SIGNUP_USERNAME,
                TEST_ENCODED_PASSWORD,
                ROLE_USER
        );
        savedUser.setId(1);

        when(userRepository.existsUserByUsername(SIGNUP_USERNAME)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // fake security user & authorities
        SecurityUser securityUser = new SecurityUser(savedUser);
        when(jpaUserDetailsService.loadUserByUsername(SIGNUP_USERNAME)).thenReturn(securityUser);
        when(tokenService.generateToken(any(), eq(SIGNUP_USERNAME))).thenReturn(TEST_JWT_TOKEN);

        // calling the method we are testing
        TokenDTO tokenDto = classUnderTest.signupNewUser(userSignupDTO);

        // verify interactions
        verify(userRepository).existsUserByUsername(SIGNUP_USERNAME);
        verify(passwordEncoder).encode(SIGNUP_PASSWORD);
        verify(jpaUserDetailsService).loadUserByUsername(SIGNUP_USERNAME);
        verify(tokenService).generateToken(any(), eq(SIGNUP_USERNAME));

        // assert the return value
        assertThat(tokenDto.getToken()).isEqualTo(TEST_JWT_TOKEN);
        assertThat(tokenDto.getUser().getUsername()).isEqualTo(SIGNUP_USERNAME);
        assertThat(tokenDto.getUser().getId()).isEqualTo(1);

        verifyNoMoreInteractions(userRepository, passwordEncoder, jpaUserDetailsService, tokenService);
    }

    @Test
    void getUserByUsername() {

    }

    @Test
    void loginUser() {

    }

    @Test 
    void deleteUser() {

    }
}
