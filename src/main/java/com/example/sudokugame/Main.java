package com.example.sudokugame;

import com.example.sudokugame.model.SudokuBoard;
import com.example.sudokugame.view.HomeStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {

    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("main");
        HomeStage.getInstance();
    }
}

