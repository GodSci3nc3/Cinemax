package com.example.policine.controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.policine.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    // Solo los elementos que REALMENTE existen en el FXML
    @FXML private ImageView logoImage;
    @FXML private Label titleLabel;
    @FXML private Label loadingLabel;
    @FXML private ProgressBar progressBar;

    private Timeline loadingAnimation;
    private String[] loadingMessages = {
            "Iniciando aplicación...",
            "Cargando módulos del sistema...",
            "Configurando base de datos...",
            "Cargando recursos multimedia...",
            "Verificando conexiones...",
            "Preparando interfaz de usuario...",
            "Optimizando rendimiento...",
            "Finalizando carga..."
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startAnimations();
        startLoadingProcess();
    }

    private void startAnimations() {
        // Verificar que los elementos existen antes de animar
        if (logoImage != null) {
            // Logo rotation con smooth easing
            RotateTransition logoRotation = new RotateTransition(Duration.seconds(3), logoImage);
            logoRotation.setFromAngle(0);
            logoRotation.setToAngle(360);
            logoRotation.setCycleCount(Timeline.INDEFINITE);
            logoRotation.setInterpolator(Interpolator.EASE_BOTH);
            logoRotation.play();
        }

        if (titleLabel != null) {
            // Title animation con scale y fade
            FadeTransition titleFade = new FadeTransition(Duration.seconds(2), titleLabel);
            titleFade.setFromValue(0);
            titleFade.setToValue(1);

            ScaleTransition titleScale = new ScaleTransition(Duration.seconds(2), titleLabel);
            titleScale.setFromX(0.5);
            titleScale.setFromY(0.5);
            titleScale.setToX(1.0);
            titleScale.setToY(1.0);
            titleScale.setInterpolator(Interpolator.EASE_OUT);

            // Bounce effect para el título
            TranslateTransition titleBounce = new TranslateTransition(Duration.seconds(2), titleLabel);
            titleBounce.setFromY(-20);
            titleBounce.setToY(0);
            titleBounce.setInterpolator(Interpolator.EASE_OUT);

            ParallelTransition titleAnimation = new ParallelTransition(titleFade, titleScale, titleBounce);
            titleAnimation.play();
        }

        if (loadingLabel != null) {
            // Loading text pulsing animation
            FadeTransition loadingPulse = new FadeTransition(Duration.seconds(1.8), loadingLabel);
            loadingPulse.setFromValue(0.7);
            loadingPulse.setToValue(1.0);
            loadingPulse.setCycleCount(Timeline.INDEFINITE);
            loadingPulse.setAutoReverse(true);
            loadingPulse.setDelay(Duration.seconds(2));
            loadingPulse.setInterpolator(Interpolator.EASE_BOTH);

            ScaleTransition loadingScale = new ScaleTransition(Duration.seconds(1.8), loadingLabel);
            loadingScale.setFromX(0.98);
            loadingScale.setFromY(0.98);
            loadingScale.setToX(1.02);
            loadingScale.setToY(1.02);
            loadingScale.setCycleCount(Timeline.INDEFINITE);
            loadingScale.setAutoReverse(true);
            loadingScale.setDelay(Duration.seconds(2));
            loadingScale.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition loadingAnimationPT = new ParallelTransition(loadingPulse, loadingScale);
            loadingAnimationPT.play();
        }
    }

    private void startLoadingProcess() {
        Task<Void> loadingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < loadingMessages.length; i++) {
                    final int index = i;
                    final double progress = (double) (i + 1) / loadingMessages.length;

                    Platform.runLater(() -> {
                        // Solo actualizar si el elemento existe
                        if (loadingLabel != null) {
                            loadingLabel.setText(loadingMessages[index]);
                        }

                        if (progressBar != null) {
                            // Smooth progress bar animation
                            Timeline progressAnimation = new Timeline(
                                    new KeyFrame(Duration.seconds(1.2),
                                            new KeyValue(progressBar.progressProperty(), progress, Interpolator.EASE_BOTH))
                            );
                            progressAnimation.play();
                        }

                        // Text transition animations solo si el elemento existe
                        if (loadingLabel != null) {
                            FadeTransition labelFade = new FadeTransition(Duration.millis(400), loadingLabel);
                            labelFade.setFromValue(0.4);
                            labelFade.setToValue(1.0);
                            labelFade.setInterpolator(Interpolator.EASE_OUT);
                            labelFade.play();

                            TranslateTransition labelSlide = new TranslateTransition(Duration.millis(400), loadingLabel);
                            labelSlide.setFromY(5);
                            labelSlide.setToY(0);
                            labelSlide.setInterpolator(Interpolator.EASE_OUT);
                            labelSlide.play();
                        }
                    });

                    // Variable loading time simulation
                    int baseDelay = 900;
                    int randomDelay = (int)(Math.random() * 700);
                    Thread.sleep(baseDelay + randomDelay);
                }

                // Final completion effect
                Platform.runLater(() -> {
                    if (progressBar != null) {
                        Timeline finalProgress = new Timeline(
                                new KeyFrame(Duration.seconds(0.8),
                                        new KeyValue(progressBar.progressProperty(), 1.0, Interpolator.EASE_OUT))
                        );
                        finalProgress.setOnFinished(e -> {
                            if (loadingLabel != null) {
                                loadingLabel.setText("¡Listo para comenzar!");

                                // Success animation
                                ScaleTransition successPulse = new ScaleTransition(Duration.millis(300), loadingLabel);
                                successPulse.setFromX(1.0);
                                successPulse.setFromY(1.0);
                                successPulse.setToX(1.15);
                                successPulse.setToY(1.15);
                                successPulse.setCycleCount(2);
                                successPulse.setAutoReverse(true);
                                successPulse.setInterpolator(Interpolator.EASE_OUT);

                                FadeTransition successGlow = new FadeTransition(Duration.millis(300), loadingLabel);
                                successGlow.setFromValue(1.0);
                                successGlow.setToValue(0.7);
                                successGlow.setCycleCount(2);
                                successGlow.setAutoReverse(true);

                                ParallelTransition successAnimation = new ParallelTransition(successPulse, successGlow);
                                successAnimation.setOnFinished(event -> {
                                    Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.2), ev -> loadLoginScreen()));
                                    delay.play();
                                });
                                successAnimation.play();
                            } else {
                                // Si no hay loadingLabel, cargar directamente
                                Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.2), ev -> loadLoginScreen()));
                                delay.play();
                            }
                        });
                        finalProgress.play();
                    } else {
                        // Si no hay progressBar, cargar directamente
                        loadLoginScreen();
                    }
                });

                return null;
            }
        };

        loadingTask.setOnSucceeded(e -> {
            System.out.println("Splash screen loading completed successfully");
        });

        loadingTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                if (loadingLabel != null) {
                    loadingLabel.setText("Error al cargar la aplicación");

                    // Error animation
                    FadeTransition errorFade = new FadeTransition(Duration.seconds(0.5), loadingLabel);
                    errorFade.setFromValue(1.0);
                    errorFade.setToValue(0.6);
                    errorFade.setCycleCount(Timeline.INDEFINITE);
                    errorFade.setAutoReverse(true);
                    errorFade.play();
                }
            });
        });

        Thread loadingThread = new Thread(loadingTask);
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private void loadLoginScreen() {
        try {
            // Verificar que tenemos una scene válida
            if (titleLabel == null || titleLabel.getScene() == null) {
                System.err.println("No se puede obtener la escena actual");
                return;
            }

            Node root = titleLabel.getScene().getRoot();

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setInterpolator(Interpolator.EASE_IN);

            ScaleTransition scaleOut = new ScaleTransition(Duration.seconds(1), root);
            scaleOut.setFromX(1.0);
            scaleOut.setFromY(1.0);
            scaleOut.setToX(0.95);
            scaleOut.setToY(0.95);
            scaleOut.setInterpolator(Interpolator.EASE_IN);

            ParallelTransition exitAnimation = new ParallelTransition(fadeOut, scaleOut);
            exitAnimation.setOnFinished(e -> {
                try {
                    // Load the login screen
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/login.fxml"));
                    Parent loginRoot = loader.load();

                    Scene loginScene = new Scene(loginRoot);
                    Stage stage = (Stage) titleLabel.getScene().getWindow();

                    // Configure stage for login
                    Main.configureLoginStage(stage);

                    // Fade in animation for the new scene
                    loginRoot.setOpacity(0);
                    loginRoot.setScaleX(1.05);
                    loginRoot.setScaleY(1.05);

                    stage.setScene(loginScene);

                    FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), loginRoot);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.setInterpolator(Interpolator.EASE_OUT);

                    ScaleTransition scaleIn = new ScaleTransition(Duration.seconds(0.8), loginRoot);
                    scaleIn.setFromX(1.05);
                    scaleIn.setFromY(1.05);
                    scaleIn.setToX(1.0);
                    scaleIn.setToY(1.0);
                    scaleIn.setInterpolator(Interpolator.EASE_OUT);

                    ParallelTransition entranceAnimation = new ParallelTransition(fadeIn, scaleIn);
                    entranceAnimation.play();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    if (loadingLabel != null) {
                        loadingLabel.setText("Error al cargar pantalla de login");
                    }
                }
            });
            exitAnimation.play();

        } catch (Exception ex) {
            ex.printStackTrace();
            if (loadingLabel != null) {
                loadingLabel.setText("Error crítico de la aplicación");
            }
        }
    }

    public void stopAnimations() {
        if (loadingAnimation != null) {
            loadingAnimation.stop();
        }
    }
}