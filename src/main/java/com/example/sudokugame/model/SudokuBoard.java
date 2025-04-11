package com.example.sudokugame.model;

import java.util.ArrayList;
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
        fillBlocks(); // Llena el ArrayList con los números del tablero
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
    private boolean fillBlocks( int blockIndex) {
        sudokuArray.clear();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sudokuArray.add(board[i][j] == 0 ? "." : String.valueOf(board[i][j]));
            }
        }
    }

    //estructura de dato
    public ArrayList<String> getSudokuArray() {
        return sudokuArray;
    }
}

//hay que seguir ajustando el modelo, todavia falta el metodo de verificación de filas y columnas para que no se repitan los números