package com.github.ovchingus.ppmtool.security;

public interface SecurityConstants {

    String SIGN_UP_URLS = "/api/users/**";

    String H2_URL = "h2-console/**";

    String SECRET = "SecretKeyToGenerateJWTs";

    // Space is necessary
    String TOKEN_PREFIX = "Bearer ";

    String HEADER_STRING = "Authorization";

    // 30 secondes in millis
    long EXPIRATION_TIME = 3_000_000;
}
