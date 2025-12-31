package be.tobiridi.passwordsecurity.security;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Manipulate encryption and decryption in AES-256 algorithm.
 * <br/>
 * Using CBC block mode and PKCS7 padding.
 *
 * @author Tony
 */
public final class AESManager {
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
     * @throws GeneralSecurityException If the parameters form{@link KeyGenerator#getInstance(String, String)} or {@link KeyGenerator#init(AlgorithmParameterSpec)} are invalid.
     * @see AESManager#getKeyGenParameter()
     */
    private static SecretKey generateSecretKey() throws GeneralSecurityException {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM, ANDROID_PROVIDER);
            kg.init(getKeyGenParameter());
            return kg.generateKey();

        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new GeneralSecurityException("The parameters for the Key generator are wrong!", e);
        }
    }

    /**
     * Get the secret key stored in the Android Key Store or {@code null} if failed.
     * @return The current {@link SecretKey} presents in the Android Key Store or attempt to get a new {@link SecretKey} if is not already existing, otherwise return {@code null}.
     * @see AESManager#generateSecretKey()
     */
    private static SecretKey getSecretKeyFromAndroidKeyStore() {
        try {
            if (keyStore == null) {
                keyStore = KeyStore.getInstance(ANDROID_PROVIDER);
                keyStore.load(null);
            }
            KeyStore.SecretKeyEntry ske = (KeyStore.SecretKeyEntry) keyStore.getEntry(ALIAS_KEY, null);
            return ske == null ? generateSecretKey() : ske.getSecretKey();

        } catch (GeneralSecurityException | IOException e) {
            //if fail to load the AndroidKeyStore or generate the secret key
            return null;
        }
    }

    /**
     * Encrypt the data using the key in AES-256 format.
     * Using a custom {@link SecretKeySpec} created by the user.
     * If you need more security prefer to use {@link AESManager#getSecretKeyFromAndroidKeyStore()}.
     * @param key The raw key used to encrypt/decrypt.
     * @param plainData The data that you want to encrypt.
     * @return The encrypted data.
     * @throws GeneralSecurityException When the {@link Cipher} is incorrect.
     * @see AESManager#decrypt(byte[], byte[])
     */
    public static byte[] encrypt(byte[] key, byte[] plainData) throws GeneralSecurityException {
        try {
            if (cipher == null) {
                try {
                    cipher = Cipher.getInstance(TRANSFORMATION);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                    throw new NoSuchAlgorithmException("The transformation used is invalid!", e);
                }
            }

            try {
                //user custom secret key
                secretKey = new SecretKeySpec(key, ALGORITHM);
                //or using AndroidKeyStore
                //secretKey = getSecretKeyFromAndroidKeyStore();
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            } catch (IllegalArgumentException | InvalidKeyException e) {
                //if the key is invalid (wrong format or not correct to use with the algorithm)
                throw e;
            } catch (UnsupportedOperationException e) {
                //normally never happened because don't use WRAP/UNWRAP mode with the cipher
                throw e;
            }

            byte[] iv = cipher.getIV();
            byte[] encryptedData = cipher.doFinal(plainData);
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();

            try {
                byteArrayOut.write(iv);
                byteArrayOut.write(encryptedData);
                return byteArrayOut.toByteArray();

            } catch (IOException e) {
                //normally never happened because always have data to write
                throw e;
            }

        } catch (GeneralSecurityException e) {
            //an error occurred with the cipher initialisation or encryption
            throw new GeneralSecurityException("The cipher initialisation or encryption error!", e);
        } catch (IOException e) {
            //normally never happened
            return new byte[0];
        }
    }

    /**
     * Encrypt the data using the key in AES-256 format.
     * @param key The raw key used to encrypt/decrypt.
     * @param plainData The data that you want to encrypt.
     * @return The encrypted data in Base64 format.
     * @throws GeneralSecurityException When the {@link Cipher} is incorrect.
     * @see AESManager#encrypt(byte[], byte[])
     */
    public static String encryptToStringBase64(byte[] key, byte[] plainData) throws GeneralSecurityException {
        return Base64.encodeToString(AESManager.encrypt(key, plainData), Base64.DEFAULT);
    }

    /**
     * Decrypt the data using the key in AES-256 format.
     * Using a custom secret key created by the user.
     * If you need more security prefer to use {@link AESManager#getSecretKeyFromAndroidKeyStore()}.
     * @param key The raw key used to encrypt/decrypt.
     * @param encryptedData The encrypted data.
     * @return The decrypted data.
     * @throws GeneralSecurityException If the cipher has wrong configuration or the key is not the same when encryption.
     * @see AESManager#encrypt(byte[], byte[])
     */
    public static byte[] decrypt(byte[] key, byte[] encryptedData) throws GeneralSecurityException {
        try {
            if (cipher == null) {
                try {
                    cipher = Cipher.getInstance(TRANSFORMATION);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                    throw new NoSuchAlgorithmException("The transformation used is invalid!", e);
                }
            }

            try {
                //user custom secret key
                secretKey = new SecretKeySpec(key, ALGORITHM);
                //or using AndroidKeyStore
                //secretKey = getSecretKeyFromAndroidKeyStore();

                byte[] iv = Arrays.copyOfRange(encryptedData, 0, cipher.getBlockSize());
                byte[] data = Arrays.copyOfRange(encryptedData, cipher.getBlockSize(), encryptedData.length);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
                return cipher.doFinal(data);

            } catch (InvalidKeyException e) {
                //if the key is invalid (wrong format or not correct to use with the algorithm)
                throw e;
            } catch (RuntimeException e) {
                throw e;
            }

        } catch (BadPaddingException e) {
            //when user input a wrong key because padding is not the same when encryption
            throw new BadPaddingException("Padding different because the key used for encrypted data is not the same!");
        } catch (GeneralSecurityException e) {
            throw e;
        }
    }

    /**
     * Decrypt the data using the key in AES-256 format.
     * @param key The raw key used to encrypt/decrypt.
     * @param base64EncryptedData The encrypted data in Base64 format.
     * @return The decrypted data in Base64 format.
     * @throws GeneralSecurityException If the cipher has wrong configuration or the key is not the same when encryption.
     * @see AESManager#decrypt(byte[], byte[])
     */
    public static String decryptToStringBase64(byte[] key, String base64EncryptedData) throws GeneralSecurityException {
        byte[] data = AESManager.decrypt(key, Base64.decode(base64EncryptedData, Base64.DEFAULT));
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * Decrypt the data using the key in AES-256 format.
     * @param key The raw key used to encrypt/decrypt.
     * @param base64EncryptedData The encrypted data in Base64 format.
     * @return The decrypted data in textual format.
     * @throws GeneralSecurityException If the cipher has wrong configuration or the key is not the same when encryption.
     * @see AESManager#decrypt(byte[], byte[])
     */
    public static String decryptToString(byte[] key, String base64EncryptedData) throws GeneralSecurityException {
        byte[] data = AESManager.decrypt(key, Base64.decode(base64EncryptedData, Base64.DEFAULT));
        return new String(data, StandardCharsets.UTF_8);
    }

}
