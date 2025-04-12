package com.example.sudokugame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuBoard {

    private final int SIZE = 6;
    private final int block_rows = 2;
    private final int block_cols = 3;
    // Number of block rows and block columns.
    private final int TOTAL_BLOCK_ROWS = SIZE / block_rows; // 6/2 = 3
    private final int TOTAL_BLOCK_COLS = SIZE / block_cols; // 6/3 = 2
    private final int TOTAL_BLOCKS = TOTAL_BLOCK_ROWS * TOTAL_BLOCK_COLS; // 3 * 2 = 6

    private final List<List<Integer>> board = new ArrayList<>();
    private final Random random = new Random();

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

        //fill the board with zeros
        for (int i = 0; i < SIZE; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < SIZE; j++) {
                row.add(0);
            }
            board.add(row);
        }

        // Attempt to fill each block with a valid number.
        if (!fillBlocks(0)) {
            System.out.println("Failed to generate the Sudoku board.");
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
        for(int j = 0; j < SIZE; j++) {
            if(board.get(row).get(j) == candidate) {
                return false;
            }
        }
        for(int i = 0; i < SIZE; i++) {
            if(board.get(i).get(col) == candidate) {
                return false;
            }
        }

        // Verifica bloque 2x3
        int startRow = (row / block_rows) * block_rows;
        int startCol = (col / block_cols) * block_cols;

        for (int i = startRow; i < startRow + block_rows; i++) {
            for (int j = startCol; j < startCol + block_cols; j++) {
                if (board.get(i).get(j) == candidate) {
                    return false;
                }
            }
        }

        return true;
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
     * Returns the current Sudoku board.
     * @return a list of lists of integers representing the Sudoku board.
     */
    public List<List<Integer>> getBoard() {
        return board ;
    }
}

