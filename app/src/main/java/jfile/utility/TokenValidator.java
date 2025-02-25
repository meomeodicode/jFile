package jfile.utility;

import io.jsonwebtoken.Claims;

public interface TokenValidator {
    boolean validateToken(String token);
    Claims extractClaims(String token);
}
