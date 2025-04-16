package com.example.sudokugame.view;

import com.example.sudokugame.controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * class that represents the main window of the sudoku game
 * Load the view defined in the FXML file and set up the main scene.
 * @author Isabela bermúdez and Julieta Arteta
 * @version 1.0
 */

public class GameStage extends Stage {

    private GameController gameController;

    /**
     * Creates a new Sudoku game window
     * Load the design from the FXML file and set the title and window size.
     * @throws IOException if an error occurs while loading FXML file
     */
    public GameStage() throws IOException {
        FXMLLoader fxmlLoad2 = new FXMLLoader(getClass().getResource("/com/example/sudokugame/game2-view.fxml"));
        Parent root = fxmlLoad2.load();
        Scene scene = new Scene(root, 800, 600);
        this.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/sudokugame/icon.png")));
        setTitle("Sudoku Game");
        setScene(scene);
        show();
    }
}
