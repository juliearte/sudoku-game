package com.example.sudokugame.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeStage extends Stage {
    public HomeStage() throws IOException {
        FXMLLoader fmxlLoad = new FXMLLoader(getClass().getResource("/com/example/sudokugame/game-view.fxml"));
        Parent root = fmxlLoad.load();
        Scene scene = new Scene(root, 800, 600);
        setTitle("MiniProyecto");
        setScene(scene);
        show();
    }

}
