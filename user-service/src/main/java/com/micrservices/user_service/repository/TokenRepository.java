package com.micrservices.user_service.repository;

import com.micrservices.user_service.model.Token;
import com.micrservices.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    @Query("select t from Token t inner join User u on t.user.id = u.id  where t.user.id = :userId and t.loggedOut = false")
    List<Token> findAllAccessTokensByUser(Long userId);
    Optional<Token> findByAccessToken(String token);
    Optional<Token > findByRefreshToken(String token);
    Token findByUser(User user);
    List<Token> findAllByUser(User user);
}
