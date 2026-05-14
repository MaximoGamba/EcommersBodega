package com.uade.tpo.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.tpo.demo.entity.User;

@Repository 
public interface UserRepository extends JpaRepository<User, Long> { 
    @EntityGraph(attributePaths = { "role" }) 
    Optional<User> findByEmail(String mail); 

    @EntityGraph(attributePaths = { "role" })
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = { "role" })
    Optional<User> findByUsernameAndActiveTrue(String username);

    @EntityGraph(attributePaths = { "role" })
    Optional<User> findByEmailAndActiveTrue(String mail);
}
