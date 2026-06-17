package com.cinereserve.auth.repository;

import com.cinereserve.auth.model.RefreshToken;
import com.cinereserve.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    // Explicit JPQL DELETE — avoids Hibernate's derived-delete SELECT+loop behavior
    // which causes the old row to still exist in the DB when the new token is inserted
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :#{#user.id}")
    int deleteByUser(User user);
}
