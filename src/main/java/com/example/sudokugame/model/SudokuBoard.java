package com.example.sudokugame.model;

import java.util.*;

public class SudokuBoard {

    private final int SIZE = 6;
    private final int block_rows = 2;
    private final int block_cols = 3;
    // Number of block rows and block columns.
    private final int TOTAL_BLOCK_ROWS = SIZE / block_rows; // 6/2 = 3
    private final int TOTAL_BLOCK_COLS = SIZE / block_cols; // 6/3 = 2
    private final int TOTAL_BLOCKS = TOTAL_BLOCK_ROWS * TOTAL_BLOCK_COLS; // 3 * 2 = 6

    private List<List<Integer>> board = new ArrayList<>();
    private List<List<Integer>> solution = new ArrayList<>();
    private final Stack<List<List<Integer>>> history = new Stack<>();
    private final Random random = new Random();

    private int attemptsLeft = 3; // Number of attempts left for the player

    /**
     * SudokuBoard class constructor. calss {@code generateBoard()}
     */
    public SudokuBoard() {
        generateBoard(); // Genera el tablero de Sudoku
    }

    /**
     * Generates the initial sudoku board
     * Filles the board with zeros and then class the {@code fiillBlocks(0)} method to start
     * filling the blocks with two random numbers.
     * @see #fillBlocks(int)
     */
    private void generateBoard() {
        // 1. Inicializar solución vacía
        solution = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            solution.add(new ArrayList<>(Collections.nCopies(SIZE, 0)));
        }

        // 2. Generar solución completa
        if (!generateCompleteSolution(0, 0)) {
            System.out.println("Error al generar solución completa");
            return;
        }

        // 3. Copiar solución al tablero de juego
        board = new ArrayList<>();
        for (List<Integer> row : solution) {
            board.add(new ArrayList<>(row));
        }

        // 4. Vaciar algunas celdas para el juego
        int cellsToKeep = 12; // Ajustar para la dificultad deseada
        int cellsToClear = SIZE * SIZE - cellsToKeep;

        Random random = new Random();
        while (cellsToClear > 0) {
            int row = random.nextInt(SIZE);
            int col = random.nextInt(SIZE);

            if (board.get(row).get(col) != 0) {
                board.get(row).set(col, 0);
                cellsToClear--;
            }
        }
    }

    private void copyBoard(List<List<Integer>> source, List<List<Integer>> destination) {
        destination.clear();
        for (List<Integer> row : source) {
            destination.add(new ArrayList<>(row));
        }
    }

    /**
     * Recursively fills the 2x3 blocks of the board with two different random numbers in each block.
     * For each block, the first two cells are identified and an attempt is made to
     * place in each cell a different random number from 1 to 6. If both numbers are
     * valid in their respective cells, they are placed on the board and the method is
     * called recursively to fill the next block. If at any point valid numbers cannot
     * be placed, backtraking is applied.
     * @param blockIndex The index of the current block to be filled.
     * @return {@code true} if all blocks were filled successfully; {@code false} if not possible
     * @see #isValid(int, int, int)
     */
    private boolean fillBlocks(int blockIndex) {
        if(blockIndex == TOTAL_BLOCKS) {
            return true;
        }

        int blockRow = blockIndex / TOTAL_BLOCK_COLS;
        int blockCol = blockIndex % TOTAL_BLOCK_COLS;
        int startRow = blockRow * block_rows;
        int startCol = blockCol * block_cols;

        //get the first two cells of the block
        List<int[]> firstTwoCells = new ArrayList<>();
        outerloop:
        for(int i = startRow; i < startRow + block_rows; i++) {
            for(int j = startCol; j < startCol + block_cols; j++) {
                firstTwoCells.add(new int[]{i, j});
                if(firstTwoCells.size() == 2){
                    break outerloop;
                }
            }
        }

        //generates a list of numbers between a 1 and 6, randomly ordered
        List<Integer> numbers = new ArrayList<>();
        for(int i = 1; i <= SIZE; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers,random);

        //try putting a different number in each cell
        for(int i = 0; i < numbers.size(); i++) {
            for (int j = i + 1; j < numbers.size(); j++) {
                int num1 = numbers.get(i);
                int num2 = numbers.get(j);

                int[] cell1 = firstTwoCells.get(0);
                int[] cell2 = firstTwoCells.get(1);

                if(isValid(cell1[0],cell1[1], num1) &&
                        isValid(cell2[0], cell2[1], num2)) {

                    board.get(cell1[0]).set(cell1[1], num1);
                    board.get(cell2[0]).set(cell2[1], num2);

                    if (fillBlocks(blockIndex + 1)){
                        return true;
                    }

                    //backtrack
                    board.get(cell1[0]).set(cell1[1], 0);
                    board.get(cell2[0]).set(cell2[1], 0);
                }
            }
        }

        return false; //the block could not be filled
    }

    /**
     * Checks whether a candidate number can be placed in a specific cell of the board.
     * A number is considered valid if it is not already present in the same row, the same column,
     * or within the 2x3 block that contains the cell.
     *
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @param candidate the number to be placed in the cell
     * @return {@code true} if the number is valid in that position; {@code false} if it is already present
     * in the same row, column, or block.
     */
    public boolean isValid(int row, int col, int candidate) {
        // Debug: Print values found in row, column and block
        System.out.println("Checking validity of " + candidate + " at position (" + row + ", " + col + ")");

        // Check row
        System.out.print("Values in row " + row + ": ");
        for(int j = 0; j < SIZE; j++) {
            System.out.print(board.get(row).get(j) + " ");
            if(board.get(row).get(j) == candidate) {
                System.out.println("\nFound " + candidate + " in row at column " + j);
                return false;
            }
        }
        System.out.println();

        // Check column
        System.out.print("Values in column " + col + ": ");
        for(int i = 0; i < SIZE; i++) {
            System.out.print(board.get(i).get(col) + " ");
            if(board.get(i).get(col) == candidate) {
                System.out.println("\nFound " + candidate + " in column at row " + i);
                return false;
            }
        }
        System.out.println();

        // Check 2x3 block
        int startRow = (row / block_rows) * block_rows;
        int startCol = (col / block_cols) * block_cols;

        System.out.println("Checking block starting at (" + startRow + ", " + startCol + ")");
        System.out.print("Values in block: ");
        for (int i = startRow; i < startRow + block_rows; i++) {
            for (int j = startCol; j < startCol + block_cols; j++) {
                System.out.print(board.get(i).get(j) + " ");
                if (board.get(i).get(j) == candidate) {
                    System.out.println("\nFound " + candidate + " in block at position (" + i + ", " + j + ")");
                    return false;
                }
            }
        }
        System.out.println("\nCandidate " + candidate + " is valid at position (" + row + ", " + col + ")");
        return true;
    }

    public boolean isCorrect(int row, int col, int candidate) {
        return solution.get(row).get(col) == candidate;
    }

    public void saveStateForUndo() {
        List<List<Integer>> snapshot = new ArrayList<>();
        for (List<Integer> row : board) {
            snapshot.add(new ArrayList<>(row));
        }
        history.push(snapshot);
    }

    public void undo() {
        if (!history.isEmpty()) {
            List<List<Integer>> previous = history.pop();
            copyBoard(previous, board);
        }
    }

    /**
     * Returns the current Sudoku board.
     * @return a list of lists of integers representing the Sudoku board.
     */
    public List<List<Integer>> getBoard() {
        return board;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public void decreaseAttempts() {
        attemptsLeft--;
    }

    public boolean isGameOver() {
        return attemptsLeft <= 0 || isBoardComplete();
    }

    private boolean isBoardComplete() {
        for (List<Integer> row : board) {
            for (int val : row) {
                if (val == 0) return false;
            }
        }
        return true;
    }
    /**
     * Finds any empty cell and provides a hint for it.
     * @return a Hint object with information about an empty cell and its correct value,
     *         or null if no empty cells are found
     */
    public Hint getHint() {
        System.out.println("Buscando una celda vacía para dar una pista...");
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board.get(row).get(col) == 0) {
                    // Get the correct value from the solution
                    int correct = solution.get(row).get(col);

                    // Verify that the value is valid (between 1 and 6)

                    if (correct >= 1 && correct <= SIZE) {
                        // Return the hint
                        System.out.println("Celda vacía encontrada (" + row + ", " + col + ")");
                        System.out.println("Valor correcto: " + correct);
                        return new Hint(row, col, correct);
                    } else {
                        System.out.println("Valor incorrecto en la celda (" + row + ", " + col + ")");
                    }
                }
            }
        }
        System.out.println("No se encontraron celdas vacías.");
        return null;
    }

    /**
     * Provides a hint for a specific cell.
     * @param targetRow row of the cell for which to provide a hint
     * @param targetCol column of the cell for which to provide a hint
     * @return a Hint object with information about the correct value for the cell,
     *         or null if the cell is already filled or no hint is available
     */
    public Hint getHintForCell(int targetRow, int targetCol) {
        // Check if the selected cell is empty
        if (targetRow >= 0 && targetRow < SIZE && targetCol >= 0 && targetCol < SIZE) {
            // Check if the cell is empty
            if (board.get(targetRow).get(targetCol) == 0) {
                int correctValue = solution.get(targetRow).get(targetCol);
                // Check if the correct value is valid (between 1 and 6)
                if (correctValue >= 1 && correctValue <= SIZE) {
                    // Return the hint
                    System.out.println("Pista solicitada para celda (" + targetRow + ", " + targetCol + ")");
                    System.out.println("Correct value from solution: " + correctValue);
                    return new Hint(targetRow, targetCol, correctValue);
                } else {
                    System.out.println("Número incorrecto (" + targetRow + ", " + targetCol + ")");
                    return null;
                }
            }
        }
        return null;
    }


    /**
     * Prints the generated board to the console.
     */
    public void printBoard() {
        for (List<Integer> row : board) {
            for (Integer num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

    public static class Hint {
        public final int row;
        public final int col;
        public final int value;

        public Hint(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }

    private boolean isValidInSolution(int row, int col, int num) {
        // Verificar fila
        for (int j = 0; j < SIZE; j++) {
            if (solution.get(row).get(j) == num) {
                return false;
            }
        }

        // Verificar columna
        for (int i = 0; i < SIZE; i++) {
            if (solution.get(i).get(col) == num) {
                return false;
            }
        }

        // Verificar bloque 2x3
        int blockStartRow = (row / 2) * 2;
        int blockStartCol = (col / 3) * 3;

        for (int i = blockStartRow; i < blockStartRow + 2; i++) {
            for (int j = blockStartCol; j < blockStartCol + 3; j++) {
                if (solution.get(i).get(j) == num) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean generateCompleteSolution(int row, int col) {
        if (row == SIZE) {
            return true;  // Tablero completo
        }

        if (col == SIZE) {
            return generateCompleteSolution(row + 1, 0);
        }

        // Si la celda ya está llena, pasar a la siguiente
        if (solution.get(row).get(col) != 0) {
            return generateCompleteSolution(row, col + 1);
        }

        // Probar números del 1 al 6 en orden aleatorio
        List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        Collections.shuffle(numbers);

        for (int num : numbers) {
            if (isValidInSolution(row, col, num)) {
                solution.get(row).set(col, num);

                if (generateCompleteSolution(row, col + 1)) {
                    return true;
                }

                // Backtrack
                solution.get(row).set(col, 0);
            }
        }

        return false;
    }
}