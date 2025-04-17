package com.example.sudokugame.controller;

import com.example.sudokugame.model.SudokuBoard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameController {

    @FXML
    private GridPane sudokuPanel;
    @FXML
    private Button helpButton;
    @FXML
    private Label hintsLeftLabel;

    private SudokuBoard sudokuBoard;
    private final int SIZE = 6;
    private TextField[][] cellFields = new TextField[SIZE][SIZE];
    private TextField selectedCell = null;

    /**
     * Initializes the game controller.
     * This method is automatically called by JavaFX when the FXML file is loaded.
     * It creates a new Sudoku board, prints it to the console, and displays it in the UI.
     */
    @FXML
    public void initialize() {
        sudokuBoard = new SudokuBoard();
        sudokuBoard.printBoard();
        updateHintsLeftDisplay();
        Platform.runLater(() -> {
            showBoard();
        });
    }

    /**
     * Updates the display of the remaining hints in the interface
     * This method gets the current number of available hints with {@code sudokuBoard.getAttemptsLeft()} and updates
     * the label and tooltip with that amount
     */
    private void updateHintsLeftDisplay() {
        int hintsLeft = sudokuBoard.getAttemptsLeft();
        if (hintsLeftLabel != null) {
            hintsLeftLabel.setText(String.valueOf(hintsLeft));
        }
        helpButton.setTooltip(new Tooltip("Pistas restantes: " + hintsLeft));
    }

    /**
     * handles the help button action
     * If hints are available, it applies a hint to the selected cell, if it is empty, or to the first empty cell it finds.Then, it updates the interface
     * deducts a hint, and checks if the sudoku has been completed correctly.
     */

    @FXML
    private void handleHelp() {
        // Check if there are attempts left
        if (sudokuBoard.getAttemptsLeft() <= 0) {
            showAlert("Pistas", "¡Se han agotado tus pistas!");
            return;
        }

// Get currently selected cell or find first empty cell
        SudokuBoard.Hint hint = null;
        if (selectedCell != null) {
            // Find the coordinates of the selected cell
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (cellFields[row][col] == selectedCell) {
                        String currentValue = selectedCell.getText();
                        if (currentValue == null || currentValue.isEmpty()) {
                            // If the selected cell is empty, get hint for this cell
                            hint = sudokuBoard.getHintForCell(row, col);
                        } else {
                            // If the selected cell already has a value, show error
                            showError("Error", "La celda ya tiene un número.");
                            return;
                        }
                        break;
                    }
                }
                if (hint != null) break;
            }
        }

        // If no cell is selected or the selected cell already has a value, find any empty cell
        if (hint == null) {
            hint = sudokuBoard.getHint();
        }

        // If a hint is found, apply it
        if (hint != null) {
            // Check if the hint value is between 1 and 6
            if (hint.value < 1 || hint.value > 6) {
                System.err.println("Error: El número de la pista no es válido." + hint.value);
                showError("Error"," Debe ser un número entre 1 y 6.");
                return;
            }

            // Decrease attempts only ONCE when a hint is actually used
            sudokuBoard.decreaseAttempts();
            updateHintsLeftDisplay();

            // Apply the hint
            TextField target = cellFields[hint.row][hint.col];
            target.setText(String.valueOf(hint.value));

            target.setStyle("-fx-background-color: rgba(147, 0, 183, 0.2); -fx-opacity: 1.0; " +
                    "-fx-text-fill: #9300B7; -fx-font-weight: bold;");

            // Update the board data structure
            sudokuBoard.getBoard().get(hint.row).set(hint.col, hint.value);

            // Show attempts left
            int attemptsLeft = sudokuBoard.getAttemptsLeft();
            showAlert("Pista aplicada", "Pista aplicada correctamente. Te quedan " +
                    attemptsLeft + " " + (attemptsLeft == 1 ? "pista" : "pistas") + ".");

            // Check if the board is complete and correct after hint
            if (isBoardComplete() && isBoardCorrect()) {
                showAlert("¡Felicitaciones!", "¡Has completado el Sudoku correctamente!");
            }
        } else {
            showAlert("Mensaje", "Tablero completo, no hay celdas vacías para proporcionar pistas.");
        }
    }

    /**
     * Displays the Sudoku board in the graphical user interface.
     * This method creates a {@link TextField} for each cell of the board, configures
     * its size, font, color, and alignment.
     * If the cell contains a number greater than zero, the number is shown and the
     * field is disabled to prevent editing. Otherwise, the field is left blank and enabled for user input.
     * Also adds listeners to adapt cell sizes when the window is resized.
     */
    private void showBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = new TextField();

                // Center the content perfectly
                textField.setAlignment(Pos.CENTER);

                // Adjust the size so it fits perfectly within the gridPane cells
                double cellWidth = sudokuPanel.getWidth() / SIZE;
                double cellHeight = sudokuPanel.getHeight() / SIZE;

                textField.setPrefSize(cellWidth, cellHeight);
                textField.setMinSize(cellWidth, cellHeight);
                textField.setMaxSize(cellWidth, cellHeight);

                // set font size proportionally to cell size (30% of the width)
                double fontSize = Math.min(cellWidth, cellHeight) * 0.5;
                Font poppinsBold = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), fontSize);
                textField.setFont(poppinsBold);

                // Style with no visible borders to blend with the griPane
                String baseStyle = "-fx-background-color: transparent; " +
                        "-fx-text-fill: #9300B7; " +
                        "-fx-padding: 0; " +
                        "-fx-background-insets: 0; " +
                        "-fx-alignment: center;";
                textField.setStyle(baseStyle);

                // Allow to the cell to be selectable
                textField.setFocusTraversable(true);

                int number = sudokuBoard.getBoard().get(row).get(col);
                if (number > 0) {
                    textField.setText(String.valueOf(number));
                    textField.setDisable(true);
                    textField.setStyle(baseStyle + " -fx-opacity: 1.0;");
                } else {
                    textField.setText("");
                }

                // Add the texfield to the gridPane with uniform margins
                sudokuPanel.add(textField, col, row);
                cellFields[row][col] = textField;
                configureTextField(textField, row, col);
            }
        }

        // Ensure cells resize and stay centered when the gridPane is resized
        sudokuPanel.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateCellSizes();
        });

        sudokuPanel.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateCellSizes();
        });
    }

    /**
     * Configures the events of a {@link TextField} in the Sudoku board to handle cell selection and user input
     * On click, selects the cell and applies a visual style. When entering text, validates that the value is between
     * 1 and 6, updates the board if valid, and applies and appropriate visual style
     * it also displays error messages if the value is invalid and checks if the game has ended correctly.
     * @param textField the cell to be configured
     * @param row the row index of the cell
     * @param col the column index of the cell
     */
    private void configureTextField(TextField textField, int row, int col) {
        // Configure click event to select the cell
        textField.setOnMouseClicked(event -> {
            // Deselect previously selected cell, if any
            if (selectedCell != null) {
                restoreDefaultStyle(selectedCell);
            }

            // Select the current cell
            selectedCell = textField;

            // Apply selection style
            String currentStyle = textField.getStyle();
            textField.setStyle(currentStyle + "-fx-background-color: rgba(147, 0, 183, 0.15);");
        });

        // Configure text input event to validate and update the board
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                // if content is cleared, update the model with zero
                sudokuBoard.getBoard().get(row).set(col, 0);
                return;
            }

            // Validate that only digits 1 to 6 are allowed
            if (!newValue.matches("[1-6]")) {
                textField.setText(oldValue); // Restore previous value
                return;
            }

            int number = Integer.parseInt(newValue);

            Font poppinsBold = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 20);

            boolean isValid = sudokuBoard.isValid(row, col, number);

            // Updated based on validation

            String baseStyle = "-fx-background-color: transparent; " +
                    "-fx-text-fill: #9300B7; " +
                    "-fx-padding: 0; " +
                    "-fx-background-insets: 0; " +
                    "-fx-alignment: center;";

            if (isValid) {
                textField.setFont(poppinsBold);
                textField.setStyle(baseStyle + "-fx-font-weight: bold;");
                sudokuBoard.getBoard().get(row).set(col, number);
            } else {
                textField.setFont(poppinsBold);
                textField.setStyle(baseStyle + "-fx-background-color: rgba(255,0,0,0.3); -fx-text-fill: red; -fx-font-weight: bold;");
                String errorMessage = identifySudokuError(row, col, number);
                showError("Número repetido", errorMessage);
                textField.setText(oldValue);
            }

            // Update the model with the entered number
            if (isValid){
                sudokuBoard.getBoard().get(row).set(col, number);
            } else {
                textField.setText(oldValue);
            }

            // check if the game is complete
            if (isBoardComplete() && isBoardCorrect()) {
                showAlert("¡Felicitaciones!", "¡Has completado el Sudoku correctamente!");
            }
        });
    }

    /**
     * Checks whether a candidate number can be placed in a specific cell of the board.
     * A number is considered valid if it is not already present in the same row, the same column,
     * or within the 2x3 block that contains the cell.
     *
     * @param row       the row index of the cell
     * @param col       the column index of the cell
     * @param candidate the number to be placed in the cell
     * @return {@code true} if the number is valid in that position; {@code false} if it is already present
     * in the same row, column, or block.
     */
    public boolean isValid(int row, int col, int candidate) {
        // Check if the candidate is already in the row
        for (int j = 0; j < SIZE; j++) {
            if (j != col && sudokuBoard.getBoard().get(row).get(j) == candidate) {
                return false;
            }
        }

        // Check if the candidate is already in the column
        for (int i = 0; i < SIZE; i++) {
            if (i != row && sudokuBoard.getBoard().get(i).get(col) == candidate) {
                return false;
            }
        }

        // Check if the candidate is already in the 2x3 block
        int startRow = (row / 2) * 2;
        int startCol = (col / 3) * 3;

        for (int i = startRow; i < startRow + 2; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (i != row && j != col && sudokuBoard.getBoard().get(i).get(j) == candidate) {
                    return false;
                }
            }
        }

        return true;

    }

    /**
     * Dynamically adjusts the size of all cells on the board when the gridPane changes size.
     */
    private void updateCellSizes() {
        double cellWidth = sudokuPanel.getWidth() / SIZE;
        double cellHeight = sudokuPanel.getHeight() / SIZE;
        double fontSize = Math.min(cellWidth, cellHeight) * 0.5;

        Font poppinsBold = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), 20);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = cellFields[row][col];
                textField.setPrefSize(cellWidth, cellHeight);
                textField.setMinSize(cellWidth, cellHeight);
                textField.setMaxSize(cellWidth, cellHeight);
                textField.setFont(poppinsBold);
            }
        }
    }
    /**
     * Restores the default style of a cell, keeping the block styles
     * @param cell the cell to which the style is restored
     */
    private void restoreDefaultStyle(TextField cell) {
        // Restore default editable cell style
        String baseStyle = "-fx-background-color: transparent; " +
                "-fx-text-fill: #9300B7; " +
                "-fx-padding: 0; " +
                "-fx-background-insets: 0; " +
                "-fx-alignment: center;";

        cell.setStyle(baseStyle);
    }

    /**
     * Identifies the reason why a number input in a sudoku cell is invalid
     * @param row cell row
     * @param col cell column
     * @param number Number to validate
     * @return A message indicating the conflict encountered or that the number is invalid
     */
    private String identifySudokuError(int row, int col, int number) {
        // Check row
        for (int j = 0; j < SIZE; j++) {
            if (j != col && sudokuBoard.getBoard().get(row).get(j) == number) {
                return "El número " + number + " ya existe en la misma fila" + (row + 1);
            }
        }

        // Check column
        for (int i = 0; i < SIZE; i++) {
            if (i != row && sudokuBoard.getBoard().get(i).get(col) == number) {
                return "El número " + number + " ya existe en la misma columna";
            }
        }

        // Check 2x3 block
        int startRow = (row / 2) * 2;
        int startCol = (col / 3) * 3;

        for (int i = startRow; i < startRow + 2; i++) {
            for (int j = startCol; j < startCol + 3; j++) {
                if (i != row && j != col && sudokuBoard.getBoard().get(i).get(j) == number) {
                    return "El número " + number + " ya existe en el mismo bloque";
                }
            }
        }

        return "El número no es válido";
    }

    /**
     * Displays an error dialog box with a specified title and message
     * @param title Title of the error window
     * @param message Error message to be displayed to the user
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Displays an Alert dialog box with a specified title and message
     * @param title Title of the alert window
     * @param message Alert message to be displayed to the user
     */

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * checkh if all the cells of the board are full
     * @return {@code true} if all cells are full, {@code false} if there is at least one empty cell
     */
    private boolean isBoardComplete() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (sudokuBoard.getBoard().get(i).get(j) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * check if all the numbers on the board are correct according to sudoku rules
     * @return {@code true} if the entire board iis valid, {@code false} otherwise
     */
    private boolean isBoardCorrect() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int number = sudokuBoard.getBoard().get(i).get(j);
                if (!sudokuBoard.isCorrect(i, j, number)) {
                    return false;
                }
            }
        }
        return true;
    }

}