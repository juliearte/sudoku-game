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


    public SudokuBoard() {
        generateBoard(); // Genera el tablero de Sudoku
    }

    // Método generar el tablero de sudoku
    private void generateBoard() {

        // Llenar con ceros inicialmente
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


    //Metodo para llenar los bloques del tablero con dos numeros aleatorios
    private boolean fillBlocks(int blockIndex) {
        if(blockIndex == TOTAL_BLOCKS) {
            return true;
        }

        int blockRow = blockIndex / TOTAL_BLOCK_COLS;
        int blockCol = blockIndex % TOTAL_BLOCK_COLS;
        int startRow = blockRow * block_rows;
        int startCol = blockCol * block_cols;

        //obtner las dos primeras celdas del bloque
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

        //generar una lista de numeros entre el 1 hasta el 6, ordenados aleatoriamente
        List<Integer> numbers = new ArrayList<>();
        for(int i = 1; i <= SIZE; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers,random);

        //Intentar cololar cada par de numeros diferentes en las dos celdas
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

        return false; //no se pudo llenar el bloque
    }
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

    //estructura de dato
    public List<List<Integer>> getBoard() {
        return board ;
    }
}

//hay que seguir ajustando el modelo, todavia falta el metodo de verificación de filas y columnas para que no se repitan los números