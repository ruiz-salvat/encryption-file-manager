package UI;

import Application.PrimaryStage;
import DataObjetcs.DataFileDto;
import Domain.DataFile;
import Domain.PrivateKey;
import UI.Dialogs.Dialog;
import Util.Constants;
import Util.FileStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainMenu {

    private static MainMenu mainMenu;
    private Button readKeyButton;
    private Button readFileButton;
    private Button createKeyButton;
    private Label readKeyLabel;
    private Label readFileLabel;
    private VBox readKeyBox;
    private VBox readFileBox;
    private VBox createKeyBox;
    private VBox actionsHBox;
    private VBox infoVBox;
    private Label infoKeyLabel;
    private Label infoFileLabel;
    private HBox editFileVBox;
    private HBox createFileBox;
    private Button editFileButton;
    private Button createFileButton;
    private HBox globalVBox;
    private Pane pane;
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private MainMenu()
    {
        readKeyButton = new Button("Read Key");
        readFileButton = new Button("Read File");
        createKeyButton = new Button("Create Key");
        editFileButton = new Button("Edit File");
        createFileButton = new Button("Create File");

        readKeyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!PrivateKey.load()) {
                    Dialog dialog = new Dialog("Error", "Error reading file.");
                    dialog.showDialog();
                } else {
                    DataFile.initialize();
                    readKeyLabel.setText(LocalDateTime.now().format(dateTimeFormatter) + " Private key read");
                    infoKeyLabel.setText("The private key <" + PrivateKey.getName() + "> was set");
                    readFileLabel.setText("No file was read yet");
                    infoFileLabel.setText("No file loaded yet");
                }
            }
        });
        readFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (PrivateKey.isSet()) {
                    // Chose file
                    FileChooser fileChooser = new FileChooser();
                    File selectedFile = fileChooser.showOpenDialog(PrimaryStage.getPrimaryStage());

                    // Read file
                    try {
                        String jsonFile = FileStorage.readFile(selectedFile.getPath());

                        ObjectMapper objectMapper = new ObjectMapper();
                        DataFileDto dataFileDto = objectMapper.readValue(jsonFile, DataFileDto.class);

                        String fileContent = PrivateKey.decrypt(dataFileDto.content);
                        DataFile.setNewDataFile(dataFileDto.name, fileContent);

                        readFileLabel.setText(LocalDateTime.now().format(dateTimeFormatter) + " Data file read");
                        infoFileLabel.setText("The data file <" + DataFile.getName() + "> was read");
                    } catch (FileNotFoundException | JsonProcessingException | NoSuchPaddingException |
                            UnsupportedEncodingException | NoSuchAlgorithmException e) {
                        Dialog dialog = new Dialog("Error", "Wrong file format.");
                        dialog.showDialog();
                    }
                } else {
                    Dialog dialog = new Dialog("Error", "No private key has been read.");
                    dialog.showDialog();
                }
            }
        });
        createKeyButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    NameSetter nameSetter = new NameSetter("Introduce the name of the key:");
                    nameSetter.showDialog();

                    try {
                        PrivateKey.createNewPrivateKey(nameSetter.getName());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                        Dialog dialog = new Dialog("Error", "Name not set.");
                        dialog.showDialog();
                    }

                    if (PrivateKey.save(nameSetter.getName())) {
                        infoKeyLabel.setText("The private key <" + PrivateKey.getName() + "> was set");
                    }
                    else
                        infoKeyLabel.setText("Error saving private key");
                } catch (NoSuchAlgorithmException | NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        });
        createFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (PrivateKey.isSet()) {
                    FileEditor fileEditor = new FileEditor(null);
                    fileEditor.openFileEditor();
                } else {
                    Dialog dialog = new Dialog("Error", "Private key not set.");
                    dialog.showDialog();
                }
            }
        });
        editFileButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileEditor fileEditor;
                if (DataFile.isSet()) {
                    fileEditor = new FileEditor(DataFile.getContent());
                    fileEditor.openFileEditor();
                } else {
                    Dialog dialog = new Dialog("Error", "Data file not read.");
                    dialog.showDialog();
                }
            }
        });

        readKeyLabel = new Label("No key was read yet");
        readKeyLabel.setPadding(new Insets(Constants.SMALL_PADDING, 0, 0, 0));
        readFileLabel = new Label("No file was read yet");
        readFileLabel.setPadding(new Insets(Constants.SMALL_PADDING, 0, 0, 0));

        readKeyBox = new VBox(readKeyButton, readKeyLabel);
        readFileBox = new VBox(readFileButton, readFileLabel);
        createKeyBox = new VBox(createKeyButton);
        editFileVBox = new HBox(editFileButton);
        createFileBox = new HBox(createFileButton);

        readKeyBox.setPadding(new Insets(Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING));
        readFileBox.setPadding(new Insets(Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING));
        createKeyBox.setPadding(new Insets(Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING));
        editFileVBox.setPadding(new Insets(Constants.BIG_PADDING, Constants.SMALL_PADDING, Constants.BIG_PADDING, Constants.SMALL_PADDING));
        createFileBox.setPadding(new Insets(Constants.BIG_PADDING, Constants.SMALL_PADDING, Constants.BIG_PADDING, Constants.SMALL_PADDING));

        actionsHBox = new VBox(readKeyBox, readFileBox);

        infoKeyLabel = new Label("No key loaded yet");
        infoKeyLabel.setPadding(new Insets(Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING));
        infoFileLabel = new Label("No file loaded yet");
        infoFileLabel.setPadding(new Insets(Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING));
        infoVBox = new VBox(infoKeyLabel, infoFileLabel, editFileVBox, createFileBox);
        infoVBox.setPadding(new Insets(Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING, Constants.BIG_PADDING));

        globalVBox = new HBox(createKeyBox, actionsHBox, infoVBox);

        pane = new Pane(globalVBox);
    }

    public static void initialize()
    {
        if (mainMenu == null) {
            mainMenu = new MainMenu();
        }
    }

    public static Pane getPane()
    {
        return mainMenu.pane;
    }
}
