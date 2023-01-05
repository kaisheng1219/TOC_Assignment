package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import javafx.util.Callback;
import model.*;
import util.GrammarConverter;

public class Rg2FaController {
    private NFA nfa;
    private DFA dfa;
    private ArrayList<State> states;
    private ArrayList<Character> symbols;
    private TextArea definition ;
    private TableView<State> transitionTb;

    @FXML
    private TextArea inputArea;
    @FXML
    private AnchorPane contentPane;
    @FXML
    private HBox cHbox;

    @FXML
    private void OnClickImportBtn() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setTitle("Open Text File");
        File selectedFile = fileChooser.showOpenDialog(contentPane.getScene().getWindow());
        inputArea.setText(util.FileExtractor.extractDataFromFile(selectedFile));
    }

    @FXML
    private void OnClickClearBtn() {
        // Reset everything
        inputArea.clear();
        cHbox.getChildren().clear();
        GrammarConverter.getAvailableStateNames().clear();
    }

    @FXML
    private void OnClickNFABtn() {
        nfa = new NFA();
        states = nfa.getStates();
        symbols = nfa.getSymbols();
        GrammarConverter grammarConverter = new GrammarConverter(nfa, inputArea.getText().replace(" ", ""));
        inputArea.setText(grammarConverter.getLongForm());
        initDefinitionTextArea();
        definition.setText(nfa.getDefinition());
        initTableView(true);
        cHbox.getChildren().clear();
        cHbox.getChildren().addAll(definition, transitionTb);
    }

    @FXML
    private void OnClickNFAEpsBtn() {
        cHbox.getChildren().clear();
        nfa.removeEpsilonTransition();
        states = nfa.getStates();
        definition.setText(nfa.getDefinition());
        this.initTableView(true);
        cHbox.getChildren().addAll(definition, transitionTb);
    }

    @FXML
    private void OnClickDFABtn() {
        cHbox.getChildren().clear();
        dfa = new DFA();
        nfa.toDFA(dfa);
        states = dfa.getStates();
        for (State s : states) {
            System.out.println(s + ": " + s.getTransitions());
        }
        this.initTableView(false);
        cHbox.getChildren().addAll(transitionTb);
    }

    @FXML
    private void OnClickMinDFABtn() {
        cHbox.getChildren().clear();
        dfa.minimize();
        this.states = dfa.getStates();
        this.initTableView(false);
        cHbox.getChildren().addAll(transitionTb);
    }

    @FXML
    private void OnClickTestBtn() throws IOException {
        cHbox.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource("Scenes/test-view.fxml"));
        fxmlLoader.setControllerFactory(type -> {
            if (type == TestController.class) {
                TestController controller = new TestController();
                controller.setData(dfa);
                return controller;
            }
            return null;
        });
        cHbox.getChildren().add(fxmlLoader.load());
    }

    @FXML
    private void initialize() {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("\\e", "Ɛ");
        replacements.put("->", "➞");
        Pattern pattern = Pattern.compile("\\S+");
        inputArea.textProperty().addListener((observable, oldValue, newValue) -> {
            StringBuilder sb = new StringBuilder();
            Matcher matcher = pattern.matcher(newValue);
            int lastEnd = 0;
            while (matcher.find()) {
                int startIndex = matcher.start();
                if (startIndex > lastEnd)
                    sb.append(newValue, lastEnd, startIndex); // add missing whitespace chars

                // replace text, if necessary
                String group = matcher.group();
                String result = replacements.get(group);
                sb.append(result == null ? group : result);

                lastEnd = matcher.end();
            }
            sb.append(newValue.substring(lastEnd));
            inputArea.setText(sb.toString());
        });
    }

    private void initDefinitionTextArea() {
        definition = new TextArea();
        definition.setFont(Font.font("SanSerif", 20));
        definition.setEditable(false);
        definition.setPadding(new Insets(cHbox.getHeight() / 4, 0, 0, 5));
        definition.setMaxWidth(cHbox.getWidth() / 2 - 100);
        definition.setMinWidth(cHbox.getWidth() / 2 - 150);
        definition.setMinHeight(20 * 6);
        definition.setStyle("-fx-background-color: transparent; " +
                "-fx-font-weight: Bold; -fx-font-size: 20; text-area-border-color: transparent;" +
                "-fx-text-alignment: center;");
    }

    private void initTableView(boolean isNFA) {
        transitionTb = new TableView<>();
        transitionTb.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transitionTb.setFixedCellSize(50);
        transitionTb.setSelectionModel(null);

        ArrayList<TableColumn<State, String>> columns = new ArrayList<>();
        TableColumn<State, String> column1 = new TableColumn<>("δ");

        column1.setCellValueFactory(cellData -> {
            String data = "";
            if (cellData.getValue().isStartState())
                data += "➞";
            if  (cellData.getValue().isFinalState())
                data += "* ";
            data += cellData.getValue();
            return new SimpleStringProperty(data);
        });
        columns.add(column1);
        String replacementLeft = isNFA ? "{" : "";
        String replacementRight = isNFA ? "}" : "";

        for (Character c : symbols) {
            TableColumn<State, String> column = new TableColumn<>(c.toString());
            columns.add(column);
            column.setCellValueFactory(cellData -> {
                Set<State> result = cellData.getValue().getTransition(c);
                String output = result == null ?
                        "∅" :
                        result.toString().replace("[", replacementLeft).replace("]", replacementRight);
                return new SimpleStringProperty(output);
            });
            column.setCellFactory(new Callback<>() {
                public TableCell<State, String> call(TableColumn param) {
                    return new TableCell<>() {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (!isEmpty()) {
                                if (item.contains("∅"))
                                    this.setStyle("-fx-font-size: 40px;");
                                setText(item);
                            }
                        }
                    };
                }
            });
            column.setSortable(false);
        }
        ObservableList<State> statesTb = FXCollections.observableArrayList(states);
        transitionTb.setItems(statesTb);
        transitionTb.getColumns().addAll(columns);
        transitionTb.setMaxHeight((transitionTb.getItems().size() + 1) * 50 * 1.01);
        transitionTb.setEditable(false);
    }
}