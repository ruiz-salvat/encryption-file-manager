package UI;

import UI.Dialogs.Dialog;
import Util.Constants;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static Util.Constants.MAX_KEY_NAME_LENGTH;

public class NameSetter {

    private Pane pane;
    private VBox vBox;
    private Label label;
    private TextField textField;
    private HBox textFieldHBox;
    private Button button;
    private HBox buttonHBox;
    private Scene scene;
    private Stage stage;

    public NameSetter(String labelText) {
        label = new Label(labelText);
        label.setPadding(new Insets(Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING));

        textField = new TextField();
        textFieldHBox = new HBox(textField);
        textFieldHBox.setPadding(new Insets(Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING));

        button = new Button("Save");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (textField.getText().length() > MAX_KEY_NAME_LENGTH) {
                    Dialog dialog = new Dialog("Error", "The number of characters is too big.\n" +
                            "Insert a key name with less than 25 characters.");
                    dialog.showDialog();
                } else
                    stage.close();
            }
        });
        buttonHBox = new HBox(button);
        buttonHBox.setPadding(new Insets(Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING));

        vBox = new VBox(label, textFieldHBox, buttonHBox);

        pane = new Pane(vBox);

        scene = new Scene(pane, Constants.HELPER_WINDOW_SIZE_X, Constants.HELPER_WINDOW_SIZE_Y);

        stage = new Stage();

        stage.setTitle("Set name");
        stage.setResizable(false);

        stage.setScene(scene);
    }

    public void showDialog() {
        stage.showAndWait();
    }

    public String getName() throws NoSuchFieldException {
        if (textField.getText() != null && !textField.getText().equals(""))
            return textField.getText();
        else
            return null;
    }
}
