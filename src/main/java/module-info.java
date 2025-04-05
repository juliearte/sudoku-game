module com.example.sudokugame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.sudokugame to javafx.fxml;
    opens com.example.sudokugame.controller to javafx.fxml;
    exports com.example.sudokugame;
}