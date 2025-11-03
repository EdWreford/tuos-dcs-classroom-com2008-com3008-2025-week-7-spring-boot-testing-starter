package com.example.data_example.controller;

import com.example.data_example.domain.Post;
import com.example.data_example.dto.PostDTO;
import com.example.data_example.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping({"", "/"})
    public ResponseEntity<List<PostDTO>> getPosts() {
        return ResponseEntity.status(HttpStatus.OK).body(postService
                .getPosts()
                .stream()
                .map(Post::toDto)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(id).toDto());
    }

    @PostMapping({"", "/"})
    public ResponseEntity<PostDTO> createPost(Authentication authentication, @RequestBody PostDTO postDTO) {
        PostDTO createdPost = postService.createPost(postDTO.toEntity(), authentication).toDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(Authentication authentication, @PathVariable int id) {
        postService.deletePost(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updateUser(Authentication authentication,
                                              @PathVariable int id,
                                              @RequestBody PostDTO updatedPost) {
        return ResponseEntity.ok(postService.updatePost(id, updatedPost.toEntity(), authentication).toDto());
    }

    @GetMapping("/byusername")
    public ResponseEntity<List<PostDTO>> getPostsByUser(@RequestParam String username) {
        return ResponseEntity.status(HttpStatus.OK).body(postService
                .getPostsByUser(username)
                .stream()
                .map(Post::toDto)
                .toList());
    }

    @GetMapping("/paged")
    public ResponseEntity<List<PostDTO>> getPostsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        Sort sortBy = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortBy);
        return ResponseEntity.status(HttpStatus.OK).body(postService
                .getPostsPaged(pageable)
                .stream()
                .map(Post::toDto)
                .toList());
    }
}
