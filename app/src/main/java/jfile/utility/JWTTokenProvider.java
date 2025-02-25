package jfile.utility;

import io.jsonwebtoken.*;
import net.bytebuddy.description.ByteCodeElement.Token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jfile.utility.Keyloader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTTokenProvider implements TokenGenerator, TokenValidator {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private static final Logger logger = LoggerFactory.getLogger(JWTTokenProvider.class);
    private static final long JWT_EXPIRATION = 3600000;

    public JWTTokenProvider(Keyloader keyLoader) throws Exception 
    {
        this.privateKey = keyLoader.loadPrivateKey();   
        this.publicKey = keyLoader.loadPublicKey();
    }
    
    @Override
    public String generateToken(String userId, String username)
    {
        Date genDate = new Date();
        Date expiry = new Date(genDate.getTime() + JWT_EXPIRATION);
        return Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("username", username)
                    .setIssuedAt(genDate)
                    .setExpiration(expiry)
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
    }
    
    @Override
    public boolean validateToken(String token) throws JwtException {
        try {
            if (token == null || token.isEmpty()) {
                throw new IllegalArgumentException("Token cannot be empty");
            }
            
            Claims claims = extractClaims(token);
            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                throw new ExpiredJwtException(null, null, "Token has expired");
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            throw e; 
        }
    }

    @Override 
    public Claims extractClaims(String token)
    {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
}
