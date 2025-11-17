package com.example.data_example.controller;


import com.example.data_example.config.Authorities;
import com.example.data_example.domain.Post;
import com.example.data_example.domain.SecurityUser;
import com.example.data_example.domain.User;
import com.example.data_example.repository.PostRepository;
import com.example.data_example.repository.UserRepository;
import com.example.data_example.service.JpaUserDetailsService;
import com.example.data_example.service.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.http.HttpHeaders;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.http.MediaType;

@SpringBootTest // This says “Start the entire application context exactly like the real application"
@AutoConfigureMockMvc // Allows us to wire a MockMvc object to help us test endpoints
@ActiveProfiles("test") // Using the environment defined in application-test.properties
class PostControllerIT {

    @Autowired
    MockMvc mockMvc; // used for testing endpoints

    @Autowired
    PostRepository postRepository; // These are real classes (i.e. not mocks) we can use for setting up tests

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenService tokenService;

    @Autowired
    JpaUserDetailsService userDetailsService;

    private String bearer; // This will be used for authenticating with endpoints

    private User aliceUser; // A user

    @BeforeEach // This marks the method to run before each test
    void setup() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        User aliceUser = userRepository.save(new User("Alice", passwordEncoder.encode("password"), Authorities.ROLE_USER));
        postRepository.save(new Post("First", "Hello", aliceUser));
        postRepository.save(new Post("Second", "Hello again", aliceUser));

        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(aliceUser.getUsername());
        String jwt  = tokenService.generateToken(securityUser.getAuthorities(), securityUser.getUsername());
        bearer = "Bearer " + jwt;
    } 

    @Test // A Test method, an entry point same as what was used for unit tests.
    void getPosts_withValidJwt_showsPosts() throws Exception {
        mockMvc.perform(get("/posts/").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].body").exists());
    }

    @Test
    void getPosts_withoutValidJwt_isUnauthorised() throws Exception {
        mockMvc.perform(get("/posts"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("")); // asserting nothing is returned
    }

    @Test
    void createPost_withValidPost_isAccepted() throws Exception {
        String json = """
            {
                "title": "Third",
                "body": "Hello yet again."
            }
        """;

        mockMvc.perform(post("/posts")
                .header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Third"))
                .andExpect(jsonPath("$.body").value("Hello yet again."))
                .andExpect(jsonPath("$.userId").value(aliceUser.getId()));
    }
}