package UI.Dialogs;

import Util.Constants;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dialog {

    private Pane pane;
    private Label label;
    private Button button;
    protected HBox buttonHBox;
    private VBox vBox;
    private Scene scene;
    protected Stage stage;

    public Dialog(String title, String body) {
        label = new Label(body);
        label.setPadding(new Insets(Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING));

        button = new Button("Ok");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        buttonHBox = new HBox(button);
        buttonHBox.setPadding(new Insets(Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING, Constants.SMALL_PADDING));

        vBox = new VBox(label, buttonHBox);

        pane = new Pane(vBox);

        scene = new Scene(pane, Constants.HELPER_WINDOW_SIZE_X, Constants.HELPER_WINDOW_SIZE_Y);

        stage = new Stage();

        stage.setTitle(title);
        stage.setResizable(false);

        stage.setScene(scene);
    }

    public void showDialog() {
        stage.showAndWait();
    }
}
