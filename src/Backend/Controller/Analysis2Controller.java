package Backend.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class Analysis2Controller {
    String folder;

    @FXML
    private Label warning;

    @FXML
    public void getFolder(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Vyberte priečinok");

        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            warning.setTextFill(Color.web("#000000"));
            warning.setText("Priečinok: " + selectedDirectory.getAbsolutePath());
            folder = selectedDirectory.getAbsolutePath();
        } else {
            warning.setTextFill(Color.web("#FF0000"));
            warning.setText("Chybný priečinok!");
            folder = null;
        }
    }

    public void analyzeCodes(ActionEvent event) {
        Alert warning = new Alert(Alert.AlertType.WARNING);
        if (folder == null) {
            warning.setContentText("Nie je vybraný priečinok alebo vybraný priečinok je chybný!");
            warning.setHeaderText("Nesprávny priečinok!");
            warning.setTitle("Upozornenie");
            warning.show();
        }
        else {
            System.out.println("Analyzujem kódy!");
        }
    }
}
