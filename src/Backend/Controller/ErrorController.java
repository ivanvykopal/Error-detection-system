package Backend.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class ErrorController {
    @FXML
    ComboBox<String> comboBox;

    @FXML
    public void getSelectedItem() {
        System.out.println(comboBox.getSelectionModel().getSelectedIndex());
    }
}
