package org.bibliotecafxv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        URL url = getClass().getResource("/org/bibliotecafxv/view/login.fxml");

        System.out.println("Ruta encontrada: " + url);

        if (url == null) {
            System.out.println("ERROR: No encontró el login.fxml");
            return;
        }

        FXMLLoader loader = new FXMLLoader(url);

        Scene scene = new Scene(loader.load());
        stage.setTitle("Biblioteca");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}