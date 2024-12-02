package cat.uvic.teknos.library.clients.test;

import cat.uvic.teknos.library.clients.console.exceptions.RequestException;
import cat.uvic.teknos.library.clients.console.utils.RestClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rawhttp.core.*;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private RestClientImpl restClient;
    private CryptoUtils cryptoUtils;

    @BeforeEach
    void setUp() {
        // Initialize RestClient and CryptoUtils
        restClient = new RestClientImpl("localhost", 8080); // Assuming the server is running on localhost and port 8080
        cryptoUtils = new CryptoUtils();
    }

    @Test
    void testGetRequest() throws RequestException {
        // Perform the GET request to fetch a single author by ID
        String response = restClient.get("/author/1", String.class);

        // Assert the response is not null and contains expected information
        assertNotNull(response);
        assertTrue(response.contains("Author Name")); // Ensure the response contains expected author data
    }

    @Test
    void testPostRequest() throws RequestException {
        // The data for creating a new author
        String body = "{\"firstName\": \"George\", \"lastName\": \"Orwell\"}";

        // Perform the POST request to create a new author
        restClient.post("/author", body);

        // Assert the POST request was successful (this would be based on the server's behavior)
        // You can check for logs, database changes, or any other side effects here
    }

    @Test
    void testEncryptSymmetricKey() throws RequestException {
        // Generate a new symmetric key
        SecretKey secretKey = cryptoUtils.createSecretKey();

        // Encrypt the symmetric key with the server's public key
        String encryptedSymmetricKey = restClient.encryptSymmetricKey(secretKey);

        // Assert the encrypted symmetric key is not null
        assertNotNull(encryptedSymmetricKey);
    }

    @Test
    void testEncryptDecryptSymmetricData() {
        String plainText = "Hello, world!";
        SecretKey secretKey = cryptoUtils.createSecretKey();

        // Encrypt the data
        String encryptedText = cryptoUtils.encrypt(plainText, secretKey);
        assertNotNull(encryptedText);

        // Decrypt the data
        String decryptedText = cryptoUtils.decrypt(encryptedText, secretKey);
        assertEquals(plainText, decryptedText);
    }

    @Test
    void testAsymmetricEncryptDecrypt() throws Exception {
        // Replace with the actual public key in Base64 format
        String publicKeyBase64 = "server-public-key-in-base64";  // Replace with actual base64-encoded public key
        PublicKey publicKey = decodePublicKey(publicKeyBase64);

        String message = "Sensitive Data";

        // Encrypt message using public key
        String encryptedMessage = cryptoUtils.asymmetricEncrypt(message, publicKey);
        assertNotNull(encryptedMessage);

        // Load the private key from a keystore (use actual private key here)
        PrivateKey privateKey = loadPrivateKeyFromKeystore("server.p12", "server", "Teknos01."); // Assuming you have a keystore and password

        // Decrypt message using private key
        String decryptedMessage = cryptoUtils.asymmetricDecrypt(encryptedMessage, privateKey);
        assertEquals(message, decryptedMessage);
    }

    // Method to decode public key from Base64 string
    private PublicKey decodePublicKey(String publicKeyBase64) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // Method to load the private key from a keystore
    private PrivateKey loadPrivateKeyFromKeystore(String keystorePath, String alias, String password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keyStore.load(fis, password.toCharArray());
        }
        return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
    }
}
