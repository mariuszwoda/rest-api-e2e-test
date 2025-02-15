package pl.where2play.restapie2etest.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Arrays;
import java.util.Date;

public class TestTokenProvider {
    public static String getTestToken() {
        return JWT.create()
                .withSubject("test-user")
                .withClaim("roles", Arrays.asList("ROLE_ADMIN"))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .sign(Algorithm.HMAC256("test-secret"));
    }
}

