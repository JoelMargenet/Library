package cat.uvic.teknos.library.cryptoutils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptoUtils {

    // Generate a hash (SHA-256) and return it as Base64
    public String getHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hash: " + e.getMessage(), e);
        }
    }

    // Create a symmetric key (AES)
    public SecretKey createSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // Use AES-256
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error creating secret key: " + e.getMessage(), e);
        }
    }

    // Decode a Base64-encoded secret key
    public SecretKey decodeSecretKey(String base64SecretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(base64SecretKey);
        return new SecretKeySpec(decodedKey, "AES");
    }

    // Encrypt a plain text using a symmetric key (AES) and return Base64-encoded encrypted text
    public String encrypt(String plainText, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting text: " + e.getMessage(), e);
        }
    }

    // Decrypt a Base64-encoded encrypted text using a symmetric key (AES)
    public String decrypt(String encryptedTextBase, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedTextBase));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting text: " + e.getMessage(), e);
        }
    }

    // Asymmetric encryption using a public key (RSA)
    public String asymmetricEncrypt(String plainTextBase64, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(Base64.getDecoder().decode(plainTextBase64));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting text asymmetrically: " + e.getMessage(), e);
        }
    }

    // Asymmetric decryption using a private key (RSA)
    public String asymmetricDecrypt(String encryptedTextBase64, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedTextBase64));
            return Base64.getEncoder().encodeToString(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting text asymmetrically: " + e.getMessage(), e);
        }
    }
}
