package com.learnspringboot.identity_user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.learnspringboot.identity_user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
