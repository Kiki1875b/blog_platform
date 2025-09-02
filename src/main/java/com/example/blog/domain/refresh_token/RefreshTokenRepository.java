package com.example.blog.domain.refresh_token;

import com.example.blog.domain.refresh_token.entity.RefreshToken;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

}
