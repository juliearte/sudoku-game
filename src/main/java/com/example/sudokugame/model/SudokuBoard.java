package com.example.sudokugame.model;

import java.util.*;

/**
 * this class represent a 6x6 sudoku board with 6 blocks of two rows and three columns
 * this class provides methods to generate a complete valid sudoku solution, generate a playabe board with some cells removed,
 * and validate user input based on sudoku rules.
 * @author Isabela bermúdez and Julieta Arteta
 *  @version 1.0
 */
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
     * SudokuBoard class constructor. calls {@code generateBoard()}
     */
    public SudokuBoard() {
        generateBoard(); // Genera el tablero de Sudoku
    }

    /**
     * Generates the initial sudoku board and generates a single valid board solution
     * This method initializes an empty solution, then generates a complete solution of the board using backtracking
     * using the {@code generateCompleteSolution(0, 0)} method. Copies the generated solution to the board of the game
     * and finally randomly empties some of its cells, leaving only 12 visible numbers, 2 numbers in each block.
     *
     * @see #generateCompleteSolution(int, int)
     */
    private void generateBoard() {
        // 1. Initializes empty solution
        solution = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            solution.add(new ArrayList<>(Collections.nCopies(SIZE, 0)));
        }

        // 2. Generates complete solution
        if (!generateCompleteSolution(0, 0)) {
            System.out.println("Error al generar solución completa");
            return;
        }

        // 3. Copy solution to the game board
        board = new ArrayList<>();
        for (List<Integer> row : solution) {
            board.add(new ArrayList<>(row));
        }
        
        // 4. clears cell leaving only 2 numbers per 2x3  region
        Random random = new Random();
        int numbersPerRegion = 2; // Numbers to keep per region

        // Iterate over each 2x3 region
        for (int regionRow = 0; regionRow < SIZE; regionRow += 2) {
            for (int regionCol = 0; regionCol < SIZE; regionCol += 3) {
                // Counter for the numbers that will be kept in this region
                int numbersKept = 0;

                // List to store the coordinates of the cells in this region
                List<int[]> cellsInRegion = new ArrayList<>();

                // collects all cells of this region
                for (int r = regionRow; r < regionRow + 2; r++) {
                    for (int c = regionCol; c < regionCol + 3; c++) {
                        cellsInRegion.add(new int[]{r, c});
                    }
                }

                // shuffle the cells to select randomly
                Collections.shuffle(cellsInRegion, random);

                // set all cells to zero except for "numbersPerRegion" of them
                for (int i = 0; i < cellsInRegion.size(); i++) {
                    int[] cell = cellsInRegion.get(i);
                    int row = cell[0];
                    int col = cell[1];

                    if (i >= numbersPerRegion) {
                        board.get(row).set(col, 0);
                    }
                }
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
     *
     * @param blockIndex The index of the current block to be filled.
     * @return {@code true} if all blocks were filled successfully; {@code false} if not possible
     * @see #isValid(int, int, int)
     */
    private boolean fillBlocks(int blockIndex) {
        if (blockIndex == TOTAL_BLOCKS) {
            return true;
        }

        int blockRow = blockIndex / TOTAL_BLOCK_COLS;
        int blockCol = blockIndex % TOTAL_BLOCK_COLS;
        int startRow = blockRow * block_rows;
        int startCol = blockCol * block_cols;

        //get the first two cells of the block
        List<int[]> firstTwoCells = new ArrayList<>();
        outerloop:
        for (int i = startRow; i < startRow + block_rows; i++) {
            for (int j = startCol; j < startCol + block_cols; j++) {
                firstTwoCells.add(new int[]{i, j});
                if (firstTwoCells.size() == 2) {
                    break outerloop;
                }
            }
        }

        //generates a list of numbers between a 1 and 6, randomly ordered
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= SIZE; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers, random);

        //try putting a different number in each cell
        for (int i = 0; i < numbers.size(); i++) {
            for (int j = i + 1; j < numbers.size(); j++) {
                int num1 = numbers.get(i);
                int num2 = numbers.get(j);

                int[] cell1 = firstTwoCells.get(0);
                int[] cell2 = firstTwoCells.get(1);

                if (isValid(cell1[0], cell1[1], num1) &&
                        isValid(cell2[0], cell2[1], num2)) {

                    board.get(cell1[0]).set(cell1[1], num1);
                    board.get(cell2[0]).set(cell2[1], num2);

                    if (fillBlocks(blockIndex + 1)) {
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
     * @param row       the row index of the cell
     * @param col       the column index of the cell
     * @param candidate the number to be placed in the cell
     * @return {@code true} if the number is valid in that position; {@code false} if it is already present
     * in the same row, column, or block.
     */
    public boolean isValid(int row, int col, int candidate) {
        // Debug: Print values found in row, column and block
        System.out.println("Checking validity of " + candidate + " at position (" + row + ", " + col + ")");

        // Check row
        System.out.print("Values in row " + row + ": ");
        for (int j = 0; j < SIZE; j++) {
            System.out.print(board.get(row).get(j) + " ");
            if (board.get(row).get(j) == candidate) {
                System.out.println("\nFound " + candidate + " in row at column " + j);
                return false;
            }
        }
        System.out.println();

        // Check column
        System.out.print("Values in column " + col + ": ");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(board.get(i).get(col) + " ");
            if (board.get(i).get(col) == candidate) {
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

    /**
     * verifies if the number entered by the user is correct.
     * It compares the candidate number provided with the corresponding value in the complete sudoku solution
     * to determine if it matches.
     *
     * @param row       Cell row to be checked
     * @param col       Column of the cell to be verified
     * @param candidate Number entered by the user to ve verified
     * @return {@code true} if the number matches the solution in that cell
     * {@code false} otherwise.
     */
    public boolean isCorrect(int row, int col, int candidate) {
        return solution.get(row).get(col) == candidate;
    }

    /**
     * Saves a copy ot the current state of the board and stacks it in {@code history} which is a stack structure
     * that saves previous versions of the board.
     */
    public void saveStateForUndo() {
        List<List<Integer>> snapshot = new ArrayList<>();
        for (List<Integer> row : board) {
            snapshot.add(new ArrayList<>(row));
        }
        history.push(snapshot);
    }

    /**
     * This method allows to undo the last move made by the player. It extracts the last state save in the {@code history}
     * stack and copies them to the current game board. If there is no previous state saved, it does nothing
     */
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

    /**
     * returns the number of the player´s remaining attempts.
     * @return the number of remaining attempts.
     */

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    /**
     * Decreases the player´s remaining attempts by one
     */
    public void decreaseAttempts() {
        attemptsLeft--;
    }

    /**
     * Check if the game is over
     * the game is considered completed if the number of  remaining attempts is less than or equal to zero, or the
     * board is completely full.
     * @return {@code true} if the game is over, otherwise {@code false}
     */
    public boolean isGameOver() {
        return attemptsLeft <= 0 || isBoardComplete();
    }

    /**
     * chech if all the cells of the board are full
     * @return {@code true} if all cells are full, {@code false} if there is at least one empty cell
     */
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

    /**
     * Internal class representing a hint for a Sudoku board cell
     */
    public static class Hint {
        public final int row;
        public final int col;
        public final int value;

        /**
         * Creates a new hint with its position (row,column) and the correct value.
         * @param row the row of the suggested cell
         * @param col the column of the suggested cell
         * @param value the correct value of the cell
         */
        public Hint(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }

    /**
     * Checks if  a candidate number can be placed in a specific cell within the sudoku solution.
     * A number is considered valid if it is not present in the same row, the same column,
     *  or in the corresponding 2x3 block within the solution
     *
     * @param row  Cell row to be checked
     * @param col  Column of the cell to be verified
     * @param num the candidate number to place in that position
     * @return {@code true} if the number is not in the same row, column or 2x3 block, {@code false} otherwise
     */
    private boolean isValidInSolution(int row, int col, int num) {
        // check row
        for (int j = 0; j < SIZE; j++) {
            if (solution.get(row).get(j) == num) {
                return false;
            }
        }

        // check column
        for (int i = 0; i < SIZE; i++) {
            if (solution.get(i).get(col) == num) {
                return false;
            }
        }

        // check block 2x3
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

    /**
     * Generates a complete valid Sudoku solution using backtracking
     * Tries number 1-6 in random order for each empty cell, ensuring no repetition in the row, column, or 2x3 block
     * using {@code isValidInSolution}
     * @param row current row index
     * @param col current column index
     * @return {@code true} if a complete solution is generated, {@code false} otherwise.
     */
    private boolean generateCompleteSolution(int row, int col) {
        if (row == SIZE) {
            return true;  // full board
        }

        if (col == SIZE) {
            return generateCompleteSolution(row + 1, 0);
        }

        // if the cell is already full, move to the next one
        if (solution.get(row).get(col) != 0) {
            return generateCompleteSolution(row, col + 1);
        }

        // test numbers 1 to 6 in random order
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