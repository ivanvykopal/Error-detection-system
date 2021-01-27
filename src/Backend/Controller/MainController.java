package Backend.Controller;

import Backend.Main;
import Frontend.Analysis1Window;
import Frontend.Analysis2Window;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    public void analysis1(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Main.closeStage();
        new Analysis1Window(stage);
    }
    @FXML
    public void analysis2(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Main.closeStage();
        new Analysis2Window(stage);
    }

}
