package com.example.sudokugame.controller;

import com.example.sudokugame.model.SudokuBoard;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class GameController {

    @FXML
    private GridPane sudokuPanel;

    private SudokuBoard sudokuBoard;

    private final int SIZE = 6;

    @FXML
    public void initialize() {
        sudokuBoard = new SudokuBoard();
        sudokuArray = sudokuBoard.getSudokuArray();
        showBoard();
    }

    private void showBoard() {
        int index = 0;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = new TextField();
                textField.setPrefSize(60, 60);

                if (!sudokuArray.get(index).equals(".")) {
                    textField.setText(sudokuArray.get(index));
                    textField.setDisable(true);
                }

                sudokuPanel.add(textField, col, row);
                index++;
            }
        }
    }
}
