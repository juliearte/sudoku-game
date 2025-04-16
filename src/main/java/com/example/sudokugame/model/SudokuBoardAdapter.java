package com.example.sudokugame.model;


public class SudokuBoardAdapter implements BoardAccess {
    private final SudokuBoard sudokuBoard;

    public SudokuBoardAdapter(SudokuBoard sudokuBoard) {
        this.sudokuBoard = sudokuBoard;
    }

    @Override
    public int getValue(int row, int col) {
        return sudokuBoard.getBoard().get(row).get(col);
    }

    @Override
    public void setValue(int row, int col, int value) {
        sudokuBoard.getBoard().get(row).set(col, value);
    }

    @Override
    public boolean isMoveValid (int row, int col, int value){
        return sudokuBoard.isValid(row, col, value);
    }

    @Override
    public boolean isCellEditable (int row, int col) {
        if(getValue(row, col) == 0){
            return true;
        } else {
            return false;
        }
    }
}
