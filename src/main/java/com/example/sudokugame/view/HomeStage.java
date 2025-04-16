package com.example.sudokugame.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeStage extends Stage {
    public HomeStage() throws IOException {
        FXMLLoader fmxlLoad = new FXMLLoader(getClass().getResource("/com/example/sudokugame/game-view.fxml"));
        Parent root = fmxlLoad.load();
        Scene scene = new Scene(root, 600, 400);
        this.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/sudokugame/icon.png")));
        setTitle("Sudoku Game");
        setScene(scene);
        setResizable(false);
        show();
    }

    //patron singleton para instanciar la ventana de inicio
    public static HomeStage getInstance() throws IOException {
        return HomeStageHolder.INSTANCE = new HomeStage();
    }

    public static void deleteInstance() {
        HomeStageHolder.INSTANCE.close();
        HomeStageHolder.INSTANCE = null;
    }

    private static class HomeStageHolder {
        private static HomeStage INSTANCE;
    }



}
