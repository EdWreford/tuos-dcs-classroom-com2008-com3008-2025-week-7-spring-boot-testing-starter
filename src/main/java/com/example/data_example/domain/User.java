package com.example.data_example.domain;

import com.example.data_example.dto.UserDTO;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users") // H2 already has a database table called "user"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String roles;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @ManyToMany
    @JoinTable(
            name = "user_friendships",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends;

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public void addFriend(User user) {
        this.friends.add(user);
        user.getFriends().add(this);
    }

    public void removeFriend(User user) {
        this.friends.remove(user);
        user.getFriends().remove(this);
    }

    public User(String username, String password, String roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User() { // Spring Data JPA requires a no arg constructor
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getRoles() {
        return roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public UserDTO toDto() {
        UserDTO dto = new UserDTO();
        dto.setId(this.id);
        dto.setUsername(this.username);
        return dto;
    }
}
