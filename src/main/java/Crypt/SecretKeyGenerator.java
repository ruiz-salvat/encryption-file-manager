package Crypt;

import Util.Constants;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SecretKeyGenerator {

    private String algorithm;
    private int keySize;

    public SecretKeyGenerator() {
        algorithm = Constants.DEFAULT_SECRET_KEY_ALGORITHM;
        keySize = Constants.DEFAULT_SECRET_KEY_SIZE;
    }

    public SecretKeyGenerator(String algorithm, int keySize) {
        this.algorithm = algorithm;
        this.keySize = keySize;
    }

    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    public SecretKey generateKey(String encodedKey) throws NoSuchAlgorithmException {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey secretKey = new SecretKeySpec(decodedKey, algorithm);
        return secretKey;
    }
}
