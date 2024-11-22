package com.example.area_backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Hash {
    private static final String ALGORITHM = "SHA-256";

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
        * Hash a element.
        *
        * @param elementToHash Element to hash.
        * @return Tuple with Left value the element hashed and Right value the salt.
    */
    public Tuple<String, String> hash(String elementToHash) throws NoSuchAlgorithmException
    {
		MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        String salt = this.generateSalt();
        String hash = Base64.getEncoder().encodeToString(md.digest((elementToHash + salt).getBytes()));
        return new Tuple<>(hash, salt);
    }

    public boolean isSame(String password, String refPassword, String salt) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        return (refPassword.equals(Base64.getEncoder().encodeToString(md.digest((password + salt).getBytes()))));
    }
}
