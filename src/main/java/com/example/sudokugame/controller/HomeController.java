package com.example.sudokugame.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;



public class HomeController {

    @FXML
    public void initialize() {
        System.out.println("hola");
    }
    @FXML
    private Button buttonPLay;

    @FXML
    void handlePlay(ActionEvent event) {
        System.out.println("Play");

    }
}
