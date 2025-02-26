package jfile.utility;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Keyloader {
    private static final Logger logger = LoggerFactory.getLogger(Keyloader.class);
    private static final String PRIVATE_KEY_PATH = "keys/private_key_access.pem";
    private static final String PUBLIC_KEY_PATH = "keys/public_key_access.pem";

    public PrivateKey loadPrivateKey() throws Exception {
        try (InputStream contentStream = getClass().getClassLoader().getResourceAsStream(PRIVATE_KEY_PATH)) {
            if (contentStream == null) {
                throw new FileNotFoundException("Private key not found in classpath: " + PRIVATE_KEY_PATH);
            }
            
            byte[] keyBytes = contentStream.readAllBytes();
            String keyString = new String(keyBytes)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
                
            byte[] encoded = Base64.getDecoder().decode(keyString);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
        }
    }

    public PublicKey loadPublicKey() throws Exception {
        try (InputStream contentStream = getClass().getClassLoader().getResourceAsStream(PUBLIC_KEY_PATH)) {
            if (contentStream == null) {
                throw new FileNotFoundException("Public key not found in classpath: " + PUBLIC_KEY_PATH);
            }
            
            byte[] keyBytes = contentStream.readAllBytes();
            String keyString = new String(keyBytes)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
                
            byte[] encoded = Base64.getDecoder().decode(keyString);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
        }
    }
}
