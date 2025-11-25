package com.authservice.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    private static final String SECRET_KEY = "secret12345";


    private static final long SESSION_TIMEOUT = 15 * 60 * 1000; // 15 min

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    private String key(String username) {
        return "token:" + username;
    }


    public String generateToken(String username, String role) {

        String token = JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000))) // JWT valid 1 day
                .sign(Algorithm.HMAC256(SECRET_KEY));

        // 🟢 Redis token timeout → 15 minutes only
        redisTemplate.opsForValue()
                .set(key(username), token, SESSION_TIMEOUT, TimeUnit.MILLISECONDS);

        return token;
    }


    public boolean isUserLoggedIn(String username) {
        return redisTemplate.hasKey(key(username));
    }


    public String validateTokenAndRetrieveSubject(String token) {
        try {
            String username = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .build()
                    .verify(token)
                    .getSubject();

            String storedToken = redisTemplate.opsForValue().get(key(username));

            if (storedToken != null && storedToken.equals(token)) {
                return username;
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }


    public void refreshSession(String username) {
        String stored = redisTemplate.opsForValue().get(key(username));

        if (stored != null) {
            redisTemplate.opsForValue()
                    .set(key(username), stored, SESSION_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }


    public void invalidateToken(String username) {
        redisTemplate.delete(key(username));
    }


    public String getToken(String username) {
        return redisTemplate.opsForValue().get(key(username));
    }
}
