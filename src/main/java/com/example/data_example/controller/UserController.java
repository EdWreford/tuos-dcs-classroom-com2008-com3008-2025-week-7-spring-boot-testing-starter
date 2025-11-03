package com.example.data_example.controller;

import com.example.data_example.domain.User;
import com.example.data_example.dto.UserDTO;
import com.example.data_example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers()
                .stream()
                .map(User::toDto)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id).toDto());
    }

    @PreAuthorize("hasAuthority(T(com.example.data_example.config.Authorities).SCOPED_ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.addFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.removeFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }

    private static final String GET_FRIENDS_URL = "/{userId}/friends";

    @GetMapping({GET_FRIENDS_URL, GET_FRIENDS_URL + "/"})
    public ResponseEntity<List<UserDTO>> getFriends(@PathVariable int userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getFriends(userId)
                .stream()
                .map(User::toDto)
                .toList());
    }
}
