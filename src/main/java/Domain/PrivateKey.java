package Domain;

import Application.PrimaryStage;
import Crypt.AESEncryptor;
import Crypt.SecretKeyGenerator;
import DataObjetcs.PrivateKeyDto;
import UI.Dialogs.Dialog;
import Util.Constants;
import Util.FileStorage;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import javafx.stage.FileChooser;

import static Util.Constants.MAX_KEY_NAME_LENGTH;


public class PrivateKey {

    private static PrivateKey instance;
    private PrivateKeyDto privateKeyDto;
    private String name;
    private SecretKey secretKey;
    private AESEncryptor aesEncryptor;

    private PrivateKey(String name, SecretKey secretKey) {
        this.name = name;
        this.secretKey = secretKey;
        try {
            aesEncryptor = new AESEncryptor(this.secretKey);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException  e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {
        instance = new PrivateKey(null, null);
    }

    public static PrivateKey getInstance() {
        return instance;
    }

    public static boolean isSet() {
        return instance.name != null && instance.secretKey != null;
    }

    public static void setNewPrivateKey(String name, SecretKey secretKey) {
        instance = new PrivateKey(name, secretKey);
        String encodedKey = Base64.getEncoder().encodeToString(instance.secretKey.getEncoded());
        instance.privateKeyDto = new PrivateKeyDto(name, encodedKey);
    }

    public static void createNewPrivateKey(String name) throws NoSuchAlgorithmException {
        SecretKeyGenerator secretKeyGenerator = new SecretKeyGenerator();
        SecretKey secretKey = secretKeyGenerator.generateKey();
        setNewPrivateKey(name, secretKey);
    }

    public static boolean save(String keyName) {
        if (keyName.length() < MAX_KEY_NAME_LENGTH) {
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            try {
                // Serialize
                String encodedKey = Base64.getEncoder().encodeToString(instance.secretKey.getEncoded());
                String jsonStr = objectWriter.writeValueAsString(new PrivateKeyDto(instance.name, encodedKey));

                // Set DTO
                instance.privateKeyDto = new PrivateKeyDto(instance.name, encodedKey);

                // Choose file location
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(keyName);
                File selectedFile = fileChooser.showSaveDialog(PrimaryStage.getPrimaryStage());

                if (selectedFile == null) {
                    instance.name = null;
                    instance.secretKey = null;
                    Dialog dialog = new Dialog("Error", "The file was not saved.\nOperation cancelled.\n" +
                            "Try again creating a new key.");
                    dialog.showDialog();
                }

                String filePath = selectedFile.getPath() + Constants.PRIVATE_KEY_FILE_EXTENSION;

                // Save file
                return FileStorage.saveFile(filePath, jsonStr);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return false;
            }
        } else
            return false;
    }

    public static void resetKey() {
        try {
            SecretKeyGenerator secretKeyGenerator = new SecretKeyGenerator();
            SecretKey secretKey = secretKeyGenerator.generateKey(instance.privateKeyDto.privateKey);
            setNewPrivateKey(instance.privateKeyDto.name, secretKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static boolean load() {
        // Choose file
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(PrimaryStage.getPrimaryStage());

        // Read file
        try {
            String fileContent = FileStorage.readFile(selectedFile.getPath());

            ObjectMapper objectMapper = new ObjectMapper();
            instance.privateKeyDto = objectMapper.readValue(fileContent, PrivateKeyDto.class);

            SecretKeyGenerator secretKeyGenerator = new SecretKeyGenerator();
            SecretKey secretKey = secretKeyGenerator.generateKey(instance.privateKeyDto.privateKey);

            setNewPrivateKey(instance.privateKeyDto.name, secretKey);

            return true;
        } catch (FileNotFoundException | JsonProcessingException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    public static String encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return instance.aesEncryptor.encrypt(text);
    }

    public static String decrypt(String cipherText) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return instance.aesEncryptor.decrypt(cipherText);
    }

    public static String getName() {
        return instance.name;
    }

    @Override
    public String toString() {
        return "PrivateKey{" +
                "name='" + name + '\'' +
                instance.privateKeyDto +
                '}';
    }
}
