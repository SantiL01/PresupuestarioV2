package com.example.presupuestariov2.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

// Hash de contraseñas con PBKDF2 + salt aleatorio (nunca se guarda la contraseña en texto plano)
public final class HashUtils {

    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    private HashUtils() {}

    public static String newSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hash(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(),
                    Base64.decode(salt, Base64.NO_WRAP), ITERATIONS, KEY_LENGTH);
            byte[] hash = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                    .generateSecret(spec).getEncoded();
            spec.clearPassword();
            return Base64.encodeToString(hash, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo calcular el hash", e);
        }
    }

    public static boolean matches(String password, String salt, String expectedHash) {
        if (password == null || salt == null || expectedHash == null) return false;
        return MessageDigest.isEqual(hash(password, salt).getBytes(), expectedHash.getBytes());
    }
}
