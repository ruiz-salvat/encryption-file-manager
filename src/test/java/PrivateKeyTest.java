import Crypt.SecretKeyGenerator;
import Domain.PrivateKey;
import org.junit.jupiter.api.Test;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrivateKeyTest {

    @Test
    void EncryptDecryptStringTest() {
        PrivateKey.initialize();
        SecretKeyGenerator secretKeyGenerator = new SecretKeyGenerator();
        try {
            PrivateKey.setNewPrivateKey("Test Key", secretKeyGenerator.generateKey());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String originalText = "Hello World";
        System.out.println("Original text: " + originalText);

        String cypherText;
        String plainText = "-1";

        try {
            cypherText = PrivateKey.encrypt(originalText);
            System.out.println("Cypher text: " + cypherText);

            plainText = PrivateKey.decrypt(cypherText);
            System.out.println(plainText);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            assertTrue(originalText.equals(plainText));
        }
    }

}
