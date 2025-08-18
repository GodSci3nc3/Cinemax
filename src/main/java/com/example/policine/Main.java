package com.example.policine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        configureSplashStage(primaryStage);

        Parent splashRoot = FXMLLoader.load(getClass().getResource("/com/example/policine/splashScreen.fxml"));
        Scene splashScene = new Scene(splashRoot, 900, 600);

        primaryStage.setScene(splashScene);
        primaryStage.show();

        centerStageOnScreen(primaryStage);
    }

    private void configureSplashStage(Stage stage) {
        stage.setTitle("Policine");
        stage.setResizable(false);

        try {
            Image icon = new Image(getClass().getResourceAsStream("/images/logo.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el ícono de la aplicación");
        }

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void centerStageOnScreen(Stage stage) {
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

        double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
        double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

        stage.setX(centerX);
        stage.setY(centerY);
    }

    public static void configureLoginStage(Stage stage) {
        stage.setTitle("Policine - Sistema de Gestión Cinematográfica");
        stage.setResizable(true);
        stage.setMinWidth(1400);
        stage.setMinHeight(800);

        try {
            Image icon = new Image(Main.class.getResourceAsStream("/images/logo.png"));
            stage.getIcons().clear();
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el ícono de la aplicación");
        }

        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double centerX = (screenBounds.getWidth() - stage.getWidth()) / 2;
        double centerY = (screenBounds.getHeight() - stage.getHeight()) / 2;

        stage.setX(centerX);
        stage.setY(centerY);
    }

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        launch(args);
    }
}