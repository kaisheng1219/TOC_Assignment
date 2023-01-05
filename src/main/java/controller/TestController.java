package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import model.*;

public class TestController {
    private DFA dfa;

    @FXML
    private TextArea inputs, status;

    public void setData(DFA dfa) {
        this.dfa = dfa;
    }

    @FXML
    private void OnClickCheckBtn() {
        status.clear();
        String[] lines = inputs.getText().split("\\R");
        for (String line : lines) {
            if (dfa.isStringAcceptedOrRejected(line))
                status.appendText("Accepted\n");
            else
                status.appendText("Rejected\n");
        }
    }
}
