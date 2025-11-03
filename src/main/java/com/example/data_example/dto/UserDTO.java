package com.example.data_example.dto;

public class UserDTO {

    private String username;

    private Integer id;

    public UserDTO() { // no-arg constructor
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
