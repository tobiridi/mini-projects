package be.tobiridi.passwordsecurity.security;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Util class with hash methods.
 * <br/>
 * The algorithm used is SHA-256.
 */
public final class HashManager {
    private static final String ALGORITHM = "SHA-256";

    /**
     * Hash the provided {@code String} into {@code byte[]}.
     * @param data The data should be hashed.
     * @return The hashed data.
     */
    public static byte[] hashStringToBytes(String data) {
        try {
            return MessageDigest.getInstance(ALGORITHM).digest(data.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            //normally never happened, if the algorithm is invalid
            return new byte[0];
        }
    }

    /**
     * Hash the provided {@code String} into Base64 {@code String}.
     * @param data The data should be hashed.
     * @return The hashed data in Base64 format.
     */
    public static String hashStringToStringBase64(String data) {
        return Base64.encodeToString(HashManager.hashStringToBytes(data), Base64.DEFAULT);
    }
}
