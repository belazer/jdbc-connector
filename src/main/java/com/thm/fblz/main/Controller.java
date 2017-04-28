package com.thm.fblz.main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.sql.ResultSet;
import java.sql.SQLException;

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

    private DatabaseModel databaseModel;


    public void connect() {
        try {
            if (databaseModel == null) {
                databaseModel = new DatabaseModel(connectionField.getText(), usernameField.getText(), passwordField.getText());
                connectButton.setText("Disconnect");
                executeButton.setDisable(false);
            } else {
                databaseModel.close();
                databaseModel = null;
                connectButton.setText("Connect");
                executeButton.setDisable(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        try {
            ResultSet rs = databaseModel.executeQuery(executeTextArea.getText());
            while (rs.next()) {
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String fname = rs.getString("fname");
                String author = rs.getString("author");
                String price = rs.getString("price");

                System.out.println(isbn + "\t" + title +
                        "\t" + fname + "\t" + author +
                        "\t" + price);
            }
        } catch (SQLException e) {
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
    }

}
