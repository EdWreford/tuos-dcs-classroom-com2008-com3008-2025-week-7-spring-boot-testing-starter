package com.example.data_example.repository;

import com.example.data_example.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("select u from User u where size(u.friends) > :x")
    List<User> findUsersWithMoreThanXFriends(@Param("x") int x);
    Optional<User> findByUsername(String username);
    List<User> findByFriendsIsEmpty();
    boolean existsUserByUsername(String username);

}
