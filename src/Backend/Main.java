package Backend;

import Frontend.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        new MainWindow(this.stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void closeStage() {
        stage.close();
    }
}