package com.example.data_example.service;

import com.example.data_example.domain.SecurityUser;
import com.example.data_example.domain.User;
import com.example.data_example.dto.TokenDTO;
import com.example.data_example.dto.UserSignupDTO;
import com.example.data_example.exceptions.UsernameExistsException;
import com.example.data_example.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class UserService {

    private static final String USER_NOT_FOUND = "User does not exist";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JpaUserDetailsService detailsService;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JpaUserDetailsService jpaUserDetailsService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.detailsService = jpaUserDetailsService;
        this.tokenService = tokenService;
    }

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public TokenDTO signupNewUser(UserSignupDTO userSignupDTO) {
        if (userRepository.existsUserByUsername(userSignupDTO.getUsername())) {
            throw new UsernameExistsException("Username already exists, please try to be original");
        }
        User newUser = new User(
                userSignupDTO.getUsername(),
                passwordEncoder.encode(userSignupDTO.getPassword()),
                ROLE_USER
        );
        User savedUser;
        savedUser = userRepository.save(newUser);
        SecurityUser securityUser = (SecurityUser) detailsService.loadUserByUsername(newUser.getUsername());
        return new TokenDTO(tokenService.generateToken(securityUser.getAuthorities(), newUser.getUsername()), savedUser.toDto());
    }

    public TokenDTO loginUser(Authentication authentication) {
        String username = authentication.getName();
        User authedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new TokenDTO(tokenService.generateToken(authentication.getAuthorities(), username), authedUser.toDto());
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersWithNoFriends() {
        return userRepository.findByFriendsIsEmpty();
    }

    public List<User> getUsersWithMoreFriendsThan(int x) {
        return userRepository.findUsersWithMoreThanXFriends(x);
    }

    public User getUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
    }

    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    public User updateUser(User updatedUser, int id) {
        User storedUser = getUser(id);
        storedUser.setUsername(updatedUser.getUsername());
        return userRepository.save(storedUser);
    }

    public void addFriend(int currentUserId, int newFriendId) {
        User currentUser = getUser(currentUserId);
        User newFriend = getUser(newFriendId);
        currentUser.addFriend(newFriend);
        userRepository.save(currentUser); // note we only need to save (update) 1 user
    }

    public void removeFriend(int currentUserId, int friendToRemoveId) {
        User currentUser = getUser(currentUserId);
        User friendToRemove = getUser(friendToRemoveId);
        currentUser.removeFriend(friendToRemove);
        userRepository.save(currentUser);
    }

    public List<User> getFriends(int userId) {
        User user = getUser(userId);
        return user.getFriends().stream().toList();
    }
}
