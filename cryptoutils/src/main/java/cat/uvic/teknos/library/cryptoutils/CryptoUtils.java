package cat.uvic.teknos.library.cryptoutils;
import javax.crypto.SecretKey;
import java.security.Key;


public class CryptoUtils {

    public String getHash(String text){
        return null; // base64
    }

    public SecretKey createSecretKey(){
        return null;
    }

    public SecretKey decodeSecretKey(String base64SecretKey){
        return null;
    }

    public String encrypt(String plainText, SecretKey key){
        return null;
    }

    public String decrypt(String encryptedTextBase, SecretKey key){
        return null;
    }

    public String asymmetricEncrypt(String plainTextBase64, Key key){
        return null;
    }

    public String asymmetricDecrypt(String encryptedTextBase64, Key key){
        return null;
    }

}
