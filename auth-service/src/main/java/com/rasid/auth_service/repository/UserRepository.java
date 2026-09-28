package com.rasid.auth_service.repository;

import com.rasid.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("select COUNT(u) from User u where u.username= :username OR u.email= :email")
    long existsByUsernameOrEmail(@Param(value = "username") String username, @Param(value = "email") String email);

    @Query("select u from User u where u.email= :email")
    Optional<User> findByEmail(@Param(value = "email") String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
}
