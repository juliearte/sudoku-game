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

    /**
     * Initializes the game controller.
     * This method is automatically called by JavaFX when the FXML file is loaded.
     * It creates a new Sudoku board, prints it to the console, and displays it in the UI.
     */
    @FXML
    public void initialize() {
        sudokuBoard = new SudokuBoard();
        sudokuBoard.printBoard();
        showBoard();
    }

    /**
     * Displays the Sudoku board in the graphical user interface.
     * This method creates a {@link TextField} for each cell of the board, configures
     * its size, font, color, and alignment.
     * If the cell contains a number greater than zero, the number is shown and the
     * field is disabled to prevent editing. Otherwise, the field is left blank and enabled for user input.
     */

    private void showBoard() {
        int index = 0;

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = new TextField();
                textField.setAlignment(Pos.CENTER);
                textField.setPrefSize(60, 60);
                textField.setFont(Font.font("basic beauty", 25));
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


    /**
     * Sets up the keyboard event for the given {@link TextField}.
     * Checks if the input is a number between 1 and 6. If valid, it calls {@code SudokuBoard#isValid(int, int, int)}
     * to check Sudoku rules. If the number is valid, it saves it to the board and disables the {@link TextField}.
     * If invalid, it changes the border color to red.
     *
     * @param textField The {@link TextField} representing a cell on the board.
     * @param row       The row index of the cell.
     * @param col       The column index of the cell.
     */
    private void HandleNumberTextField(TextField textField, int row, int col) {
        textField.setOnKeyReleased(e -> {
            String input = textField.getText();

            // Validación básica: solo permitir números del 1 al 6
            if (!input.matches("[1-6]")) {
                textField.setText("");
                System.out.println("Número no válido");
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

            System.out.println("¿Es válido?: " + sudokuBoard.isValid(row, col, number));
        });
    }
}