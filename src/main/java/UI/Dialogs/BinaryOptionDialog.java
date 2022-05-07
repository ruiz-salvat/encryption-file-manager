package UI.Dialogs;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.WindowEvent;

public class BinaryOptionDialog extends Dialog {

    private Button cancelButton;
    private boolean cancelled;

    public BinaryOptionDialog(String title, String body) {
        super(title, body);

        cancelled = false;

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                cancelled = true;
                stage.close();
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                cancelled = true;
                stage.close();
            }
        });

        this.buttonHBox.getChildren().add(cancelButton);
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
