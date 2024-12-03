package cat.uvic.teknos.library.clients;

import cat.uvic.teknos.library.clients.Exceptions.CryptoException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoUtils {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    // Generate a hash (SHA-256) and return it as Base64
    public static String getHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());
            return toBase64(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }

    // Create a symmetric key (AES)
    public static SecretKey createSecretKey() {
        try {
            return KeyGenerator.getInstance("AES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }

    // Decode a Base64-encoded secret key
    public static SecretKey decodeSecretKey(String base64SecretKey) {
        byte[] decodedKey = fromBase64(base64SecretKey);
        return new SecretKeySpec(decodedKey, "AES");
    }

    // Encrypt plain text using a symmetric key (AES)
    public static String encrypt(String plainText, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return toBase64(cipher.doFinal(plainText.getBytes()));
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    // Decrypt encrypted text (Base64) using a symmetric key (AES)
    public static String decrypt(String encryptedTextBase64, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(fromBase64(encryptedTextBase64)));
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    // Asymmetric encryption using a public key (RSA)
    public static String asymmetricEncrypt(String plainText, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = cipher.doFinal(plainText.getBytes());
            return toBase64(encryptedData);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    // Asymmetric decryption using a private key (RSA)
    public static String asymmetricDecrypt(String encryptedTextBase64, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipher.doFinal(fromBase64(encryptedTextBase64));
            return new String(decryptedData);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    // Decode a Base64-encoded public key
    public static PublicKey decodePublicKey(String base64PublicKey) {
        try {
            byte[] publicKeyBytes = fromBase64(base64PublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    // Decode a Base64-encoded private key
    public static PrivateKey decodePrivateKey(String base64PrivateKey) {
        try {
            byte[] privateKeyBytes = fromBase64(base64PrivateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    // Convert byte array to Base64-encoded string
    public static String toBase64(byte[] data) {
        return encoder.encodeToString(data);
    }

    // Convert Base64-encoded string to byte array
    public static byte[] fromBase64(String base64) {
        return decoder.decode(base64);
    }
}
