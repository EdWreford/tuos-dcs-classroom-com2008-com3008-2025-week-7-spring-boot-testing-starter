package com.example.data_example.service;

import com.example.data_example.config.Authorities;
import com.example.data_example.domain.Post;
import com.example.data_example.domain.User;
import com.example.data_example.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    private static final String POST_NOT_FOUND = "Post does not exist";

    @Autowired
    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public Post createPost(Post newPost, Authentication auth) {
        User creatingUser = userService.getUserByUsername(auth.getName());
        newPost.setUser(creatingUser);
        return postRepository.save(newPost);
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public Page<Post> getPostsPaged(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public List<Post> getPostsByUser(String username) {
        return postRepository.findPostByUser_Username(username);
    }

    public Post getPost(int postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, POST_NOT_FOUND));
    }

    private static final String NOT_AUTHED_TO_DELETE = "You are not authorised to delete this post";

    private static boolean isAdmin(Authentication auth) {
        return auth
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(Authorities.SCOPED_ADMIN));
    }

    private void checkPostOwnership(Post post, Authentication auth) {
        User authedUser = userService.getUserByUsername(auth.getName());
        if (!Objects.equals(authedUser.getId(), post.getUser().getId()) && !isAdmin(auth)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, NOT_AUTHED_TO_DELETE);
        }
    }

    public void deletePost(int postId, Authentication auth) {
        Post postToDelete = getPost(postId);
        checkPostOwnership(postToDelete, auth);
        postRepository.deleteById(postId);
    }

    public Post updatePost(int postId, Post updatedPost, Authentication auth) {
        Post storedPost = getPost(postId);
        checkPostOwnership(storedPost, auth);
        storedPost.setBody(updatedPost.getBody());
        storedPost.setTitle(updatedPost.getTitle());
        return postRepository.save(storedPost);
    }
}
