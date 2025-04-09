package com.example.sudokugame.controller;

import com.example.sudokugame.view.GameStage;
import com.example.sudokugame.view.HomeStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;


public class HomeController {

    @FXML
    public void initialize() {
        System.out.println("hola");
    }
    @FXML
    private Button buttonPLay;

    @FXML
    void handlePlay(ActionEvent event) throws IOException {
        System.out.println("Play");

        GameStage gameStage = new GameStage();
        HomeStage.deleteInstance();

    }
}
