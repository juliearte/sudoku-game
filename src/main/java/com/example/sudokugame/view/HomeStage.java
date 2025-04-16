package com.example.sudokugame.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * class that represents the initial window of the sudoku game
 * @author Isabela bermúdez and Julieta Arteta
 * @version 1.0
 */

public class HomeStage extends Stage {

    /**
     * Constructor for the HomeStage class
     * Load the FXML file containing the game view and configure the window.
     * @throws IOException if an error occurs while loading FXML file
     */
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

    /**
     * return the single instance of HomeStage and if it does not exist, creates a new one.
     * @return the single instance of HomeStage
     * @throws IOException if an error occurs while loading FXML file
     */
    public static HomeStage getInstance() throws IOException {
        return HomeStageHolder.INSTANCE = new HomeStage();
    }

    /**
     * Closes the window and sets the instance to null to allow future creation.
     */
    public static void deleteInstance() {
        HomeStageHolder.INSTANCE.close();
        HomeStageHolder.INSTANCE = null;
    }

    /**
     * Static internal class in charge of holding the single instance of HomeStage
     */

    private static class HomeStageHolder {
        private static HomeStage INSTANCE;
    }



}
