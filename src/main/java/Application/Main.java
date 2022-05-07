package Application;
import Domain.DataFile;
import Domain.PrivateKey;
import UI.MainMenu;
import Util.Constants;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private static Pane mainMenuPane;

    private static void initializeApp() {
        MainMenu.initialize();
        mainMenuPane = MainMenu.getPane();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Initialize
        initializeApp();
        PrivateKey.initialize();
        DataFile.initialize();
        PrimaryStage.initialize(stage);

        StackPane root = new StackPane();

        root.getChildren().add(mainMenuPane);

        Scene scene = new Scene(root, Constants.WINDOW_SIZE_X, Constants.WINDOW_SIZE_Y);

        scene.setFill(Color.BROWN);

        stage.setTitle("Encryption File Manager");

        stage.setScene(scene);

        stage.show();
    }
}
