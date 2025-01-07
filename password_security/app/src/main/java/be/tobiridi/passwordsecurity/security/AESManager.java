package be.tobiridi.passwordsecurity.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Manipulate encryption and decryption in AES-256 algorithm.
 * <br/>
 * Using CBC Mode and PKCS7 padding.
 *
 * @author Tony
 */
public abstract class AESManager {
    private static final String ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC;
    private static final String PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7;
    private static final String TRANSFORMATION = ALGORITHM + "/" + BLOCK_MODE + "/" + PADDING;
    private static final int KEY_SIZE = 256;
    private static final String ALIAS_KEY = "AppKey";
    private static final String ANDROID_PROVIDER = "AndroidKeyStore";
    private static Cipher cipher;
    private static SecretKey secretKey;
    private static KeyStore keyStore;

    /**
     * Describe the parameters spec for generate key.
     * @return The parameter specification for AES-256 encryption/decryption.
     * @see KeyGenParameterSpec
     */
    private static KeyGenParameterSpec getKeyGenParameter() {
        return new KeyGenParameterSpec.Builder(
                ALIAS_KEY,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setUserAuthenticationRequired(false)
                .setKeySize(KEY_SIZE)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .build();
    }

    /**
     * Generate a new AES-256 secret key and store it in the Android Key Store.
     * @return The generated secret key.
     */
    private static SecretKey generateSecretKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM, ANDROID_PROVIDER);
            kg.init(getKeyGenParameter());

            return kg.generateKey();

            //TODO: manage exception
        } catch (NoSuchAlgorithmException | NoSuchProviderException |
                 InvalidParameterException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the secret key stored in the Android Key Store.
     * @return The current key or a new secret key if is not already existing.
     */
    private static SecretKey getSecretKeyFromAndroidKeyStore() {
        try {
            if (keyStore == null) {
                keyStore = KeyStore.getInstance(ANDROID_PROVIDER);
                keyStore.load(null);
            }

            KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) keyStore.getEntry(ALIAS_KEY, null);
            return ske == null ? generateSecretKey() : ske.getSecretKey();

            //TODO: manage exception
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException |
                 UnrecoverableEntryException | CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a new Cipher object when using encrypt mode and then use it for encrypt and decrypt data.
     * Using a custom secret key created by the user
     * otherwise if you need more security prefer to use {@link AESManager#getSecretKeyFromAndroidKeyStore()}
     * @param cipherMode {@link Cipher#ENCRYPT_MODE} or {@link Cipher#DECRYPT_MODE}
     * @param key The raw secret key entered by the user.
     */
    private static void initCipher(int cipherMode, String key) {
        try {
            if (cipherMode == Cipher.ENCRYPT_MODE) {
                cipher = Cipher.getInstance(TRANSFORMATION);
                //user custom secret key
                byte[] digest = MessageDigest.getInstance("SHA256").digest(key.getBytes(StandardCharsets.UTF_8));
                secretKey = new SecretKeySpec(digest, ALGORITHM);
                //using AndroidKeyStore
                //secretKey = getSecretKeyFromAndroidKeyStore();
            }

            switch (cipherMode) {
                case Cipher.ENCRYPT_MODE: cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                    break;

                case Cipher.DECRYPT_MODE: {
                    IvParameterSpec ivSpec = new IvParameterSpec(cipher.getIV());
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
                }
                    break;
            }

            //TODO: manage exception
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Encrypt the data using the key in AES-256 format.
     * @param key The key used to encrypt/decrypt.
     * @param plainData The data that you want to encrypt.
     * @return The encrypt data.
     * @see AESManager#decrypt(String, byte[])
     */
    public static byte[] encrypt(String key, byte[] plainData) {
        initCipher(Cipher.ENCRYPT_MODE, key);

        try {
            return cipher.doFinal(plainData);

            //TODO: manage exception
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypt the data using the key in AES-256 format.
     * @param key The key used to encrypt/decrypt.
     * @param encryptData The encrypt data.
     * @return The decrypt data.
     * @see AESManager#encrypt(String, byte[])
     * @throws NullPointerException If the cipher is null
     */
    public static byte[] decrypt(String key, byte[] encryptData) throws NullPointerException {
        if (cipher == null) {
            throw new NullPointerException("The cipher is not initialized, use encryption method before using decryption method.");
        }

        initCipher(Cipher.DECRYPT_MODE, key);

        try {
            return cipher.doFinal(encryptData);

        } catch (BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }

    }

}
