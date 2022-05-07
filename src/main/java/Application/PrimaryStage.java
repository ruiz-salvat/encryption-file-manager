package Application;

import javafx.stage.Stage;
import java.lang.instrument.UnmodifiableClassException;

public class PrimaryStage {

    private static Stage stage;

    private PrimaryStage(Stage stage) {
        PrimaryStage.stage = stage;
    }

    public static void initialize(Stage stage) throws UnmodifiableClassException {
        if (PrimaryStage.stage == null) {
            PrimaryStage.stage = stage;
            PrimaryStage.stage.setResizable(false);
        } else {
            throw new UnmodifiableClassException();
        }
    }

    public static Stage getPrimaryStage() {
        return stage;
    }
}
