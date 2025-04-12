package com.example.sudokugame.controller;

import com.example.sudokugame.model.SudokuBoard;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class GameController {

    @FXML
    private GridPane sudokuPanel;
    private SudokuBoard sudokuBoard;
    private final int SIZE = 6;

    @FXML
    public void initialize() {
        sudokuBoard = new SudokuBoard();
        sudokuBoard.printBoard();
        showBoard();
    }

    private void showBoard() {
        int index = 0;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = new TextField();
                textField.setAlignment(Pos.CENTER);
                textField.setPrefSize(60, 60);
                textField.setFont(Font.font("basic beauty", 25 ));
                textField.setStyle("-fx-text-fill: black;");
                int number = sudokuBoard.getBoard().get(row).get(col);

                if (number > 0) {
                    textField.setText(String.valueOf(number));
                    textField.setDisable(true);
                } else {
                    textField.setText("");
                }

                sudokuPanel.add(textField, col, row);
                index++;
                HandleNumberTextField(textField, row, col);
            }
        }
    }
    private void HandleNumberTextField(TextField textField, int row, int col) {
        textField.setOnKeyReleased(e -> {
            String input = textField.getText();

            if (!input.matches("[1-6]") && input.equals("")) {
                textField.setText("");
                return;
            }
            int number = Integer.parseInt(input);

            if (sudokuBoard.isValid(row, col, number)) {
                sudokuBoard.getBoard().get(row).set(col, number);
                textField.setDisable(true);
                textField.setStyle("-fx-border-color: black;");
            } else {
                textField.setStyle("-fx-border-color: red;");
            }

            System.out.println(sudokuBoard.isValid(row, col, number));
        });
    }
}
