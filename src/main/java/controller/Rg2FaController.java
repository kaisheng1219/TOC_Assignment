package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;

public class Rg2FaController {
    @FXML
    private TextArea inputArea;
    @FXML
    private AnchorPane contentPane;

    @FXML
    private Button importBtn, clearBtn;

    @FXML
    private void OnClickImportBtn() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(importBtn.getScene().getWindow());
        util.FileExtractor.extractDataFromFile(selectedFile);
    }

    @FXML
    private void OnClickClearBtn() {
        inputArea.clear();
    }
}
