package com.example.sudokugame.controller;

import com.example.sudokugame.model.SudokuBoard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.util.Optional;

/**
 * Control class responsible to managing the sudoku game interface
 * It connects the UI elements to the game Logic implement in the {@link SudokuBoard}
 * @author Isabela Bermúdez and Julieta Arteta
 *  @version 1.0
 */

public class GameController {

    @FXML
    private GridPane sudokuPanel;
    @FXML
    private Button helpButton;
    @FXML
    private Label hintsLeftLabel;
    @FXML
    private ImageView livesImageView;
    @FXML
    private Button btnUndoGame;

    private SudokuBoard sudokuBoard;
    private final int SIZE = 6;
    private TextField[][] cellFields = new TextField[SIZE][SIZE];
    private TextField selectedCell = null;
    private int lives = 3; // Inicia con 3 vidas

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
        updateLivesDisplay();
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
     * Handles the undo button action, which prompts the user
     * to confirm before resetting the game.
     * If confirmed, the game is reset to its initial state.
     */


    @FXML
    private void onHandleBUndo() {
        // Show confirmation before resetting
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Reiniciar Juego");
        confirmation.setHeaderText(null);
        confirmation.setContentText("¿Estás seguro de que quieres reiniciar el juego? Se perderá tu progreso actual.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Reset the game
            resetGame();
        }
    }

    /**
     * Resets the game to its initial state:
     * - Creates a new Sudoku board
     * - Resets lives
     * - Clears the current panel
     * - Initializes the cell fields
     * - Displays the new board
     * - Updates hint and lives displays
     * - Deselects any selected cell
     */

    private void resetGame() {
        sudokuBoard = new SudokuBoard();
        lives = 3;
        sudokuPanel.getChildren().clear();
        cellFields = new TextField[SIZE][SIZE];
        showBoard();
        updateHintsLeftDisplay();
        updateLivesDisplay();
        selectedCell = null;
    }

    /**
     * Handles the help button action.
     * If the player has hints left, it proceeds to show one.
     * If no hints are left, displays an alert.
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
        double cellWidth = sudokuPanel.getWidth() / SIZE;
        double cellHeight = sudokuPanel.getHeight() / SIZE;
        double fontSize = Math.min(cellWidth, cellHeight) * 0.5;

        Font poppinsBold = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), fontSize);

        // Configure click event to select the cell
        textField.setOnMouseClicked(event -> {
            if (selectedCell != null) {
                restoreDefaultStyle(selectedCell);
            }
            selectedCell = textField;
            String currentStyle = textField.getStyle();
            textField.setStyle(currentStyle + "-fx-background-color: rgba(147, 0, 183, 0.15);");
        });

        // Configure text input event to validate and update the board
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                sudokuBoard.getBoard().get(row).set(col, 0);
                return;
            }

            // Validate that only digits 1 to 6 are allowed
            if (!newValue.matches("[1-6]")) {
                textField.setText(oldValue);
                return;
            }

            int number = Integer.parseInt(newValue);
            boolean isValid = sudokuBoard.isValid(row, col, number);

            // Updated based on validation
            String baseStyle = "-fx-background-color: transparent; " +
                    "-fx-text-fill: #9300B7; " +
                    "-fx-padding: 0; " +
                    "-fx-background-insets: 0; " +
                    "-fx-alignment: center;";

            if (isValid) {
                textField.setFont(poppinsBold); // Usar la fuente con tamaño calculado
                textField.setStyle(baseStyle + "-fx-font-weight: bold;");
                sudokuBoard.getBoard().get(row).set(col, number);
                restoreDefaultStyle(selectedCell);
            } else {
                textField.setFont(poppinsBold); // Usar la fuente con tamaño calculado
                textField.setStyle(baseStyle + "-fx-background-color: rgba(255,0,0,0.3); -fx-text-fill: red; -fx-font-weight: bold;");
                String errorMessage = identifySudokuError(row, col, number);
                showError("Número repetido", errorMessage);
                textField.setText(oldValue);
                lives--;
                updateLivesDisplay();
                restoreDefaultStyle(selectedCell);
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

        Font poppinsBold = Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Bold.ttf"), fontSize);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                TextField textField = cellFields[row][col];
                textField.setPrefSize(cellWidth, cellHeight);
                textField.setMinSize(cellWidth, cellHeight);
                textField.setMaxSize(cellWidth, cellHeight);
                textField.setFont(poppinsBold); // Actualizar la fuente con el nuevo tamaño
            }
        }
    }

    /**
     * Restores the default style of a cell, keeping the block styles
     * @param cell the cell to which the style is restored
     */
    private void restoreDefaultStyle(TextField cell) {
        // Restaurar estilo predeterminado para celdas editables
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
     * Displays an informational alert with the specified title and message.
     *
     * @param title   the title of the alert dialog
     * @param message the message to display
     */

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Check if the board is complete (no empty cells)
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

    // Check if all numbers on the board are correct
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

    /**
     * Updates the display of lives remaining in the interface
     * This method updates the image displayed based on the number of lives left
     * and closes the game window if lives reach 0
     */

    private void updateLivesDisplay() {
        String imagePath;
        switch (lives) {
            case 3:
                imagePath = "/lives3.png";
                break;
            case 2:
                imagePath = "/lives2.png";
                break;
            case 1:
                imagePath = "/lives1.png";
                break;
            default:
                imagePath = "/lives0.png";
                break;
        }

        try {
            Image livesImage = new Image(getClass().getResourceAsStream(imagePath));
            livesImageView.setImage(livesImage);

            // Cerrar el juego cuando las vidas llegan a 0
            if (lives <= 0) {
                closeGameWindow();
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de vidas: " + e.getMessage());
        }
    }

    /**
     * Closes the game window and displays a message indicating that the game is over
     * This method is called when the player loses all their lives
     */

    private void closeGameWindow() {

        Stage stage = (Stage) sudokuPanel.getScene().getWindow();
        stage.close();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("¡Se acabó el juego!");
            alert.setContentText("Has perdido todas tus vidas. ¡Inténtalo de nuevo!");
            alert.showAndWait();
        });
    }
}