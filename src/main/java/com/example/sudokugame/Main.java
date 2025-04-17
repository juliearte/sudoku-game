package com.example.sudokugame;

import com.example.sudokugame.model.SudokuBoard;
import com.example.sudokugame.view.HomeStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The entry point for the Sudoku game application.
 * This class initializes the JavaFX application and opens the main stage.
 */

public class Main extends Application {

    private static Stage primaryStage;

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args command-line arguments passed to the application
     */

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the main stage of the application and starts the game.
     * This method is called after the JavaFX runtime has initialized.
     *
     * @param stage the primary stage for this application
     * @throws IOException if there is an error loading the initial scene or resources
     */

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("main");
        HomeStage.getInstance();
    }
}

