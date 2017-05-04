package com.thm.fblz.main;

import com.sun.tools.javac.util.Name;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class Controller {
    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField connectionField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button connectButton;

    @FXML
    private Button executeButton;

    @FXML
    private TextArea executeTextArea;

    @FXML
    private TableView table;

    private DatabaseModel databaseModel;
    private ObservableList<ObservableList> data;
    private FileChooser fileChooser;

    public void connect() {
        try {
            if (databaseModel == null) {
                databaseModel = new DatabaseModel(connectionField.getText(), usernameField.getText(), passwordField.getText());
                connectButton.setText("Disconnect");
                connectButton.setTextFill(Color.GREEN);
                executeButton.setDisable(false);
                data = FXCollections.observableArrayList();
            } else {
                databaseModel.close();
                databaseModel = null;
                connectButton.setText("Connect");
                connectButton.setTextFill(Color.BLACK);
                executeButton.setDisable(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        try {
            Result result = databaseModel.executeQuery(executeTextArea.getText());

            // CLEAN DATABASE
            table.getItems().clear();
            table.getColumns().clear();

            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            for (String columnName : result.getColumnNames()) {
                TableColumn column = new TableColumn(columnName);
                int index = result.getColumnNames().indexOf(columnName);
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(index).toString());
                    }
                });
                table.getColumns().add(column);
            }

            /********************************
             * Data added to ObservableList *
             ********************************/
            for(List<String> row : result.getData()){
                ObservableList<String> observableRow = FXCollections.observableArrayList();
                observableRow.addAll(row);
                data.add(observableRow);
            }
            table.setItems(data);

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("SQL Error");
            alert.setHeaderText(e.getLocalizedMessage());
            alert.showAndWait();
        }
    }

    public void loadFile(){
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        try {
            String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            executeTextArea.setText(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveFile(){
        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        String fileContent = executeTextArea.getText();
        try {
            FileUtils.writeStringToFile(file, fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        boolean debugging = true;
        if (debugging) {
            connectionField.setText("jdbc:postgresql://localhost:5432/postgres");
            usernameField.setText("postgres");
            passwordField.setText("Passwort123");
            executeTextArea.setText("SELECT * FROM BOOKS");
        }
        fileChooser = new FileChooser();
        fileChooser.setTitle("View SQL-Files");
        fileChooser.setInitialDirectory(
                FileUtils.getUserDirectory()
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("SQL Files", "*.sql"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }

}
