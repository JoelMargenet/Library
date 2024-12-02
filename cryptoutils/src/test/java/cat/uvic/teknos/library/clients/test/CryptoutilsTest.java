package cat.uvic.teknos.library.clients.test;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class CryptoutilsTest {

    private final CryptoUtils cryptoUtils = new CryptoUtils();

    @Test
    void getHash() {
        String text = "Some text...";
        String expectedBase64Hash = "quonJ6BjRSC1DBOGuBWNdqixj8z20nuP+QH7cVvp7PI="; // Precomputed hash
        String computedHash = cryptoUtils.getHash(text);

        assertEquals(expectedBase64Hash, computedHash);
    }

    @Test
    void createSecretKey() {
        SecretKey secretKey = cryptoUtils.createSecretKey();
        assertNotNull(secretKey);

        String keyBase64 = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        System.out.println("Generated Secret Key (Base64): " + keyBase64);
    }

    @Test
    void decodeSecretKey() {
        String secretKeyBase64 = "jaruKzlE7xerbNSjxiVjZtuAeYWrcyMGsA8TaTqZ8AM="; // Example Base64 key
        SecretKey secretKey = cryptoUtils.decodeSecretKey(secretKeyBase64);

        assertNotNull(secretKey);
        assertEquals("AES", secretKey.getAlgorithm());
    }

    @Test
    void encryptAndDecryptSymmetric() {
        SecretKey secretKey = cryptoUtils.createSecretKey();
        String plainText = "This is a secret message";

        String encryptedText = cryptoUtils.encrypt(plainText, secretKey);
        assertNotNull(encryptedText);
        assertNotEquals(plainText, encryptedText); // The encrypted text must differ from the original

        String decryptedText = cryptoUtils.decrypt(encryptedText, secretKey);
        assertNotNull(decryptedText);
        assertEquals(plainText, decryptedText); // The decrypted text must match the original
    }

    @Test
    void asymmetricEncryptAndDecrypt() {
        try {
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair(); // Generate RSA key pair
            String plainText = "This is a public key encrypted message";

            // Encrypt with the public key
            String encryptedText = cryptoUtils.asymmetricEncrypt(Base64.getEncoder().encodeToString(plainText.getBytes()), keyPair.getPublic());
            assertNotNull(encryptedText);
            assertNotEquals(plainText, encryptedText); // The encrypted text must differ from the original

            // Decrypt with the private key
            String decryptedText = new String(Base64.getDecoder().decode(cryptoUtils.asymmetricDecrypt(encryptedText, keyPair.getPrivate())));
            assertNotNull(decryptedText);
            assertEquals(plainText, decryptedText); // The decrypted text must match the original
        } catch (NoSuchAlgorithmException e) {
            fail("Failed to generate RSA key pair: " + e.getMessage());
        }
    }
}
