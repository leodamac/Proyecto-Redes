package com.mycompany.decision;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Cargar la imagen
        Image tcpipImage = new Image("tcpip.jpg");
        ImageView imageView = new ImageView(tcpipImage);
        imageView.setFitWidth(600); // Ajusta el tamaño según sea necesario
        imageView.setPreserveRatio(true);

        // Crear el botón "Simular"
        Button simulateButton = new Button("Simular");
        simulateButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20 10 20;");
        
        // Efecto de sombra al botón para un aspecto más tecnológico
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#003366"));
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        simulateButton.setEffect(shadow);

        // Acción del botón
        simulateButton.setOnAction(e -> {
            // Aquí puedes llamar a la siguiente aplicación
            App app = new App();
            try {
                app.start(new Stage());
                primaryStage.close(); // Cierra la pantalla actual
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Configurar el layout
        VBox vbox = new VBox(20, imageView, simulateButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: white;");

        // Crear la escena
        Scene scene = new Scene(vbox, 800, 600); // Tamaño de la ventana
        primaryStage.setTitle("Simulación TCP/IP");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
