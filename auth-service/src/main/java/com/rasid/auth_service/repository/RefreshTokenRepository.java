package com.rasid.auth_service.repository;

import com.rasid.auth_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Query("select t from RefreshToken t where t.tokenHash= :tokenHash")
    Optional<RefreshToken> findByHashedToken(@Param("tokenHash") String tokenHash);
}
