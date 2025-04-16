package com.example.sudokugame.model;

public interface BoardAccess {
    int getValue(int row, int col);
    void setValue(int row, int col, int value);
    boolean isCellEditable(int row, int col);
    boolean isMoveValid(int row, int col, int value);
}
