package lk.ijse.dep10.studentRegister;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class AppInitializer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/StudentView.fxml"))));
        primaryStage.setTitle("JDBC Student Register");
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}