package com.example.data_example.dto;

import com.example.data_example.domain.Post;

public class PostDTO {

    private Integer id;

    private String title;

    private String body;

    private int userId;

    public PostDTO() { // no arg constructor
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Post toEntity() {
        Post entity = new Post();
        entity.setTitle(this.title);
        entity.setBody(this.body);
        if (this.id != null) {
            entity.setId(this.id);
        }
        return entity;
    }

}
