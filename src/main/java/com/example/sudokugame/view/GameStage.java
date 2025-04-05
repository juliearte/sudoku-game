package com.example.sudokugame.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GameStage extends Stage {
    public GameStage() throws IOException {
        FXMLLoader fmxlLoad2 = new FXMLLoader(getClass().getResource("/com/example/sudokugame/game2-view.fxml"));
        Parent root = fmxlLoad2.load();
        Scene scene = new Scene(root, 800, 600);
        setTitle("MiniProyecto");
        setScene(scene);
        show();
    }
}
