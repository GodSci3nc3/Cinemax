package com.example.policine.controllers;

import com.example.policine.model.dao.UsuarioDAO;
import com.example.policine.model.entities.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AuthController implements Initializable {

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

    // DAO para operaciones de base de datos
    private UsuarioDAO usuarioDAO;

    // Usuario autenticado
    private static Usuario usuarioLogueado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar el DAO
        usuarioDAO = new UsuarioDAO();

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
    private void handleLogin() throws IOException {
        String email = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();
        boolean recordarme = chkRecordarme.isSelected();

        // Validaciones básicas
        if (email.isEmpty() || contrasena.isEmpty()) {
            showAlert("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        // Validar formato de email
        if (!isValidEmail(email)) {
            showAlert("Error", "Por favor ingresa un email válido", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Intentar autenticar usuario
            Usuario usuario = usuarioDAO.autenticar(email, contrasena);

            if (usuario != null) {
                // Login exitoso
                usuarioLogueado = usuario;

                System.out.println("Login exitoso - Usuario: " + usuario.getNombre() + " " + usuario.getApellido());
                System.out.println("Recordarme: " + recordarme);

                // Limpiar campos
                clearLoginFields();

                // Navegar a la pantalla de películas
                navigateToMovieListing();

            } else {
                // Credenciales incorrectas
                showAlert("Error de Autenticación", "Email o contraseña incorrectos", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            System.err.println("Error durante el login: " + e.getMessage());
            showAlert("Error del Sistema", "Ocurrió un error durante el inicio de sesión. Intenta nuevamente.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegister() {
        String nombreCompleto = txtNombreCompleto.getText().trim();
        String email = txtEmailRegistro.getText().trim();
        String usuario = txtUsuarioRegistro.getText().trim();
        String contrasena = txtContrasenaRegistro.getText();
        String confirmarContrasena = txtConfirmarContrasena.getText();

        // Validaciones básicas
        if (nombreCompleto.isEmpty() || email.isEmpty() || usuario.isEmpty() ||
                contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            showAlert("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        // Validar formato de email
        if (!isValidEmail(email)) {
            showAlert("Error", "Por favor ingresa un email válido", Alert.AlertType.ERROR);
            return;
        }

        // Validar que las contraseñas coincidan
        if (!contrasena.equals(confirmarContrasena)) {
            showAlert("Error", "Las contraseñas no coinciden", Alert.AlertType.ERROR);
            return;
        }

        // Validar longitud de contraseña
        if (contrasena.length() < 6) {
            showAlert("Error", "La contraseña debe tener al menos 6 caracteres", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Verificar si el email ya existe
            Usuario usuarioExistente = usuarioDAO.buscarPorEmail(email);
            if (usuarioExistente != null) {
                showAlert("Error", "Ya existe un usuario registrado con este email", Alert.AlertType.ERROR);
                return;
            }

            // Separar nombre y apellido
            String[] partesNombre = nombreCompleto.split("\\s+", 2);
            String nombre = partesNombre[0];
            String apellido = partesNombre.length > 1 ? partesNombre[1] : "";

            // Crear nuevo usuario (usar el usuario como teléfono por ahora)
            Usuario nuevoUsuario = new Usuario(nombre, apellido, email, contrasena, usuario);

            // Insertar usuario en la base de datos
            boolean registroExitoso = usuarioDAO.insertar(nuevoUsuario);

            if (registroExitoso) {
                showAlert("Éxito", "Usuario registrado correctamente", Alert.AlertType.INFORMATION);

                System.out.println("Registro exitoso - Usuario: " + nombre + " " + apellido);
                System.out.println("Email: " + email);

                // Limpiar campos del registro
                clearRegisterFields();

                // Cambiar a la vista de login
                switchToLogin();

            } else {
                showAlert("Error", "Error al registrar usuario. Intenta nuevamente.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            System.err.println("Error durante el registro: " + e.getMessage());
            showAlert("Error del Sistema", "Ocurrió un error durante el registro. Intenta nuevamente.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void forgotPassword() {
        // TODO: Implementar lógica de recuperación de contraseña
        System.out.println("Recuperar contraseña");
        showAlert("Información", "Funcionalidad de recuperación de contraseña en desarrollo", Alert.AlertType.INFORMATION);
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void navigateToMovieListing() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/movieListing.fxml"));
        Parent movieListingRoot = loader.load();

        Stage currentStage = (Stage) btnLogin.getScene().getWindow();

        Scene movieListingScene = new Scene(movieListingRoot);

        currentStage.setScene(movieListingScene);
        currentStage.setTitle("Cinemax - Películas");

        currentStage.centerOnScreen();

        System.out.println("Navegación exitosa a Movie Listing");
    }

    // Método estático para obtener el usuario logueado desde otras clases
    public static Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    // Método estático para cerrar sesión
    public static void cerrarSesion() {
        usuarioLogueado = null;
    }
}