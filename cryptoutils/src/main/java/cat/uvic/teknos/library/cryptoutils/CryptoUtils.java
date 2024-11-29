package cat.uvic.teknos.library.cryptoutils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CryptoUtils {

    private static final Logger logger = Logger.getLogger(CryptoUtils.class.getName());

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

    // Load a keystore from a file (e.g., .p12)
    public KeyStore loadKeystore(String keystorePath, String password) throws Exception {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keystore.load(fis, password.toCharArray());
        }
        return keystore;
    }

    // Get private key from keystore
    public PrivateKey getPrivateKey(KeyStore keystore, String alias, String password) throws Exception {
        Key key = keystore.getKey(alias, password.toCharArray());
        if (key instanceof PrivateKey) {
            return (PrivateKey) key;
        } else {
            throw new RuntimeException("No private key found with the alias: " + alias);
        }
    }

    // Get public key from keystore
    public PublicKey getPublicKey(KeyStore keystore, String alias) throws Exception {
        Certificate cert = keystore.getCertificate(alias);
        return cert.getPublicKey();
    }

    // Export public certificate from keystore
    public void exportCertificate(KeyStore keystore, String alias, String certificatePath) throws Exception {
        Certificate cert = keystore.getCertificate(alias);
        try (FileOutputStream fos = new FileOutputStream(certificatePath)) {
            fos.write(cert.getEncoded());
        }
    }

    // Import public certificate into keystore
    public void importCertificate(KeyStore keystore, String certificatePath, String alias) throws Exception {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        try (FileInputStream fis = new FileInputStream(certificatePath)) {
            Certificate cert = certFactory.generateCertificate(fis);
            keystore.setCertificateEntry(alias, cert);
        }
    }

    // Add private key and certificate to keystore
    public void addPrivateKeyToKeystore(KeyStore keystore, String alias, PrivateKey privateKey, Certificate[] chain, String password) throws Exception {
        keystore.setKeyEntry(alias, privateKey, password.toCharArray(), chain);
        try (FileOutputStream fos = new FileOutputStream("keystore.p12")) {
            keystore.store(fos, password.toCharArray());
        }
    }

    // Initialize keystore and load client/server certificates
    public void initializeKeystore() throws Exception {
        // Load keystore
        KeyStore keystore = loadKeystore("server.p12", "password");

        // Export server certificate
        exportCertificate(keystore, "server", "server-cert.cer");

        // Import client certificate into server keystore
        importCertificate(keystore, "client-cert.cer", "client-cert");

        // Add server private key and certificate chain to keystore (for server authentication)
        PrivateKey serverPrivateKey = getPrivateKey(keystore, "server", "password");
        Certificate[] serverChain = keystore.getCertificateChain("server");
        addPrivateKeyToKeystore(keystore, "server", serverPrivateKey, serverChain, "password");

        // Save keystore after modification
        addPrivateKeyToKeystore(keystore, "client", serverPrivateKey, serverChain, "password");
    }
}
