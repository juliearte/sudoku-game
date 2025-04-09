package com.example.sudokugame.model;

import java.util.ArrayList;
import java.util.Random;

public class SudokuBoard {

    private final int SIZE = 6;
    private final int[][] board = new int[SIZE][SIZE]; //dibuja la matriz
    private final ArrayList<String> sudokuArray = new ArrayList<>(); // ArrayList para almacenar los números del Sudoku en la matriz

    public SudokuBoard() {
        generateBoard(); // Genera el tablero de Sudoku
        fillArray(); // Llena el ArrayList con los números del tablero
    }

    // Método para obtener el número en una posición específica
    private void generateBoard() {
        Random random = new Random();

        // Llenar con ceros inicialmente
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }

        // Llenar bloques de 2x3 asegurando que no se repitan en el bloque
        for (int row = 0; row < SIZE; row += 2) {
            for (int col = 0; col < SIZE; col += 3) {
                int number1, number2;
                do {
                    number1 = random.nextInt(6) + 1;
                    number2 = random.nextInt(6) + 1;
                } while (number1 == number2);

                board[row][col] = number1;
                board[row][col + 1] = number2;
            }
        }
    }

    //Falta implementar la logica para llenar el resto del tablero

    // Método para llenar el ArrayList con los números del tablero
    private void fillArray() {
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