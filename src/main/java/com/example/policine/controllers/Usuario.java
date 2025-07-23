package com.example.policine.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class Usuario implements Initializable {

    @FXML private Button btnIniciarSesion;
    @FXML private Button btnRegistrarse;
    @FXML private VBox loginForm;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private CheckBox chkRecordarme;
    @FXML private Hyperlink linkOlvidasteContrasena;
    @FXML private Button btnLogin;

    // Register Form Controls
    @FXML private VBox registerForm;
    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtEmailRegistro;
    @FXML private TextField txtUsuarioRegistro;
    @FXML private PasswordField txtContrasenaRegistro;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private Button btnRegister;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialización del controlador
        // Configurar el estado inicial (mostrar login por defecto)
        showLoginForm();
    }

    @FXML
    private void switchToLogin() {
        showLoginForm();
        updateTabButtons(true);
    }

    @FXML
    private void switchToRegister() {
        showRegisterForm();
        updateTabButtons(false);
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();
        boolean recordarme = chkRecordarme.isSelected();

        // TODO: Implementar lógica de autenticación
        System.out.println("Login - Usuario: " + usuario);
        System.out.println("Recordarme: " + recordarme);

        // Validaciones básicas
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            showAlert("Error", "Por favor completa todos los campos");
            return;
        }

        // Aquí implementarías la lógica de autenticación
        // Ejemplo: llamar a un servicio de autenticación
        // if (authService.login(usuario, contrasena)) {
        //     // Redirigir a la pantalla principal
        // } else {
        //     showAlert("Error", "Credenciales incorrectas");
        // }
    }

    @FXML
    private void handleRegister() {
        String nombreCompleto = txtNombreCompleto.getText();
        String email = txtEmailRegistro.getText();
        String usuario = txtUsuarioRegistro.getText();
        String contrasena = txtContrasenaRegistro.getText();
        String confirmarContrasena = txtConfirmarContrasena.getText();

        // TODO: Implementar lógica de registro
        System.out.println("Registro - Usuario: " + usuario);
        System.out.println("Email: " + email);

        // Validaciones básicas
        if (nombreCompleto.isEmpty() || email.isEmpty() || usuario.isEmpty() ||
                contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            showAlert("Error", "Por favor completa todos los campos");
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            showAlert("Error", "Las contraseñas no coinciden");
            return;
        }

        // Aquí implementarías la lógica de registro
        // Ejemplo: llamar a un servicio de registro
        // if (authService.register(nombreCompleto, email, usuario, contrasena)) {
        //     showAlert("Éxito", "Usuario registrado correctamente");
        //     switchToLogin();
        // } else {
        //     showAlert("Error", "Error al registrar usuario");
        // }
    }

    @FXML
    private void forgotPassword() {
        // TODO: Implementar lógica de recuperación de contraseña
        System.out.println("Recuperar contraseña");
        showAlert("Información", "Funcionalidad de recuperación de contraseña en desarrollo");
    }

    private void showLoginForm() {
        loginForm.setVisible(true);
        registerForm.setVisible(false);
    }

    private void showRegisterForm() {
        loginForm.setVisible(false);
        registerForm.setVisible(true);
    }

    private void updateTabButtons(boolean isLogin) {
        if (isLogin) {
            btnIniciarSesion.setStyle("-fx-background-color: rgba(255, 255, 255, 0.25); -fx-background-radius: 10 0 0 10; -fx-border-width: 0; -fx-text-fill: white; -fx-font-weight: bold;");
            btnRegistrarse.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 10 10 0; -fx-border-width: 0; -fx-text-fill: #B0C4DE;");
        } else {
            btnRegistrarse.setStyle("-fx-background-color: rgba(255, 255, 255, 0.25); -fx-background-radius: 0 10 10 0; -fx-border-width: 0; -fx-text-fill: white; -fx-font-weight: bold;");
            btnIniciarSesion.setStyle("-fx-background-color: transparent; -fx-background-radius: 10 0 0 10; -fx-border-width: 0; -fx-text-fill: #B0C4DE;");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearLoginFields() {
        txtUsuario.clear();
        txtContrasena.clear();
        chkRecordarme.setSelected(false);
    }

    private void clearRegisterFields() {
        txtNombreCompleto.clear();
        txtEmailRegistro.clear();
        txtUsuarioRegistro.clear();
        txtContrasenaRegistro.clear();
        txtConfirmarContrasena.clear();
    }
}