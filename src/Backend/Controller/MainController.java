package Backend.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.io.IOException;

public class MainController extends Controller {

    @FXML
    public void analysis1(ActionEvent event) throws IOException {
        showAnalysis1Window();
    }
    @FXML
    public void analysis2(ActionEvent event) throws IOException {
        showAnalysis2Window();
    }

}
