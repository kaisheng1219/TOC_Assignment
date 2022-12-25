package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class StartController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private TabPane tp;
    @FXML
    private URL location;

    @FXML
    void initialize() {
        tp.tabMinWidthProperty().bind(tp.widthProperty().divide(tp.getTabs().size()).subtract(17));
        tp.getTabs().get(1).setText("RG ➜ FA");
    }
}
