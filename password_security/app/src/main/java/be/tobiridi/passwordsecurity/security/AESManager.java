package be.tobiridi.passwordsecurity.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Arrays;

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
    private static KeyStore keyStore;
    private static Cipher cipher;
    private static SecretKey secretKey;

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
     * Encrypt the data using the key in AES-256 format.
     * Using a custom secret key created by the user.
     * If you need more security prefer to use {@link AESManager#getSecretKeyFromAndroidKeyStore()}.
     * @param key The raw key used to encrypt/decrypt.
     * @param plainData The data that you want to encrypt.
     * @return The encrypted data.
     * @see AESManager#decrypt(byte[], byte[])
     */
    public static byte[] encrypt(byte[] key, byte[] plainData) {
        try {
            if (cipher == null) {
                cipher = Cipher.getInstance(TRANSFORMATION);
                //user custom secret key
                secretKey = new SecretKeySpec(key, ALGORITHM);
                //or using AndroidKeyStore
                //secretKey = getSecretKeyFromAndroidKeyStore();
            }

            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] iv = cipher.getIV();
            byte[] encryptedData = cipher.doFinal(plainData);

            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            byteArrayOut.write(iv);
            byteArrayOut.write(encryptedData);

            return byteArrayOut.toByteArray();

            //TODO: manage exception
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Encrypt the data using the key in AES-256 format.
     * @param key The raw key used to encrypt/decrypt.
     * @param plainData The data that you want to encrypt.
     * @return The encrypted data in Base64 format.
     * @see AESManager#encrypt(byte[], byte[])
     */
    public static String encryptToStringBase64(byte[] key, byte[] plainData) {
        return Base64.encodeToString(AESManager.encrypt(key, plainData), Base64.DEFAULT);
    }

    /**
     * Decrypt the data using the key in AES-256 format.
     * Using a custom secret key created by the user.
     * If you need more security prefer to use {@link AESManager#getSecretKeyFromAndroidKeyStore()}.
     * @param key The raw key used to encrypt/decrypt.
     * @param encryptedData The encrypted data.
     * @return The decrypted data.
     * @see AESManager#encrypt(byte[], byte[])
     */
    public static byte[] decrypt(byte[] key, byte[] encryptedData) throws BadPaddingException {
        try {
            if (cipher == null) {
                cipher = Cipher.getInstance(TRANSFORMATION);
                //user custom secret key
                secretKey = new SecretKeySpec(key, ALGORITHM);
                //or using AndroidKeyStore
                //secretKey = getSecretKeyFromAndroidKeyStore();
            }

            byte[] iv = Arrays.copyOfRange(encryptedData, 0, cipher.getBlockSize());
            byte[] data = Arrays.copyOfRange(encryptedData, cipher.getBlockSize(), encryptedData.length);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

            return cipher.doFinal(data);

            //TODO: manage exception
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            //throw exception when user input a wrong key
            //because padding is not the same while encryption
            throw new BadPaddingException("Padding different because the key used for encrypted data is not the same.");
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Decrypt the data using the key in AES-256 format.
     * @param key The raw key used to encrypt/decrypt.
     * @param base64EncryptedData The encrypted data in Base64 format.
     * @return The decrypted data in Base64 format.
     * @see AESManager#decrypt(byte[], byte[])
     */
    public static String decryptToStringBase64(byte[] key, String base64EncryptedData) throws BadPaddingException {
        byte[] data = AESManager.decrypt(key, Base64.decode(base64EncryptedData, Base64.DEFAULT));
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * Decrypt the data using the key in AES-256 format.
     * @param key The raw key used to encrypt/decrypt.
     * @param base64EncryptedData The encrypted data in Base64 format.
     * @return The decrypted data in textual format.
     * @see AESManager#decrypt(byte[], byte[])
     */
    public static String decryptToString(byte[] key, String base64EncryptedData) throws BadPaddingException {
        byte[] data = AESManager.decrypt(key, Base64.decode(base64EncryptedData, Base64.DEFAULT));
        return new String(data, StandardCharsets.UTF_8);
    }

}
