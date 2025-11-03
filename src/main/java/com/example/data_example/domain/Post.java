package com.example.data_example.domain;

import com.example.data_example.dto.PostDTO;
import jakarta.persistence.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;

    private String body;

    @ManyToOne
    private User user;

    public Post(String title, String body, User user) {
        this.title = title;
        this.body = body;
        this.user = user;
    }

    public Post() {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PostDTO toDto() {
        PostDTO dto = new PostDTO();
        dto.setId(this.id);
        dto.setBody(this.body);
        dto.setTitle(this.title);
        dto.setUserId(this.user.getId());
        return dto;
    }
}
