package Crypt;

import UI.Dialogs.Dialog;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static Util.Constants.IV_LENGTH_BYTE;
import static Util.Constants.TAG_LENGTH_BIT;

public class AESEncryptor {

    private SecretKey secretKey;
    private Cipher cipher;
    private byte[] iv;

    public AESEncryptor(SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.secretKey = secretKey;
        cipher = Cipher.getInstance("AES/GCM/NoPadding");
        iv = generateIv(IV_LENGTH_BYTE);
    }

    public String encrypt(String plaintext) throws UnsupportedEncodingException {
        byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, secretKey, iv, plaintext.getBytes("UTF-8"));
        return base64(encrypted);
    }

    public String decrypt(String ciphertext) throws UnsupportedEncodingException {
        byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, secretKey, iv, base64(ciphertext));
        if (decrypted == null) {
            Dialog dialog = new Dialog("Error", "Wrong decryption key.");
            dialog.showDialog();
        }
        return new String(decrypted, "UTF-8");
    }

    private byte[] doFinal(int encryptMode, SecretKey key, byte[] iv, byte[] bytes) {
        try {
            cipher.init(encryptMode, key, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            return cipher.doFinal(bytes);
        } catch (InvalidKeyException
                | InvalidAlgorithmParameterException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String base64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    public static byte[] base64(String str) {
        return Base64.decodeBase64(str.getBytes());
    }

    private byte[] generateIv(int numBytes) {
        byte[] nonce = new byte[numBytes];
        return nonce;
    }

}
