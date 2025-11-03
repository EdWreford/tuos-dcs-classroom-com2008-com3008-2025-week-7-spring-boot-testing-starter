package com.example.data_example.dto;

public class TokenDTO {

    private String token;

    private UserDTO user;

    public TokenDTO(String token, UserDTO userDTO) {
        this.token = token;
        this.user = userDTO;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
