package com.example.policine.controllers;

import com.example.policine.model.dao.UsuarioDAO;
import com.example.policine.model.entities.Usuario;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AuthController implements Initializable {

    // Tab Buttons
    @FXML private Button btnIniciarSesion;
    @FXML private Button btnRegistrarse;

    // Login Form
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

        // Configurar efectos hover en botones
        setupHoverEffects();

        // Animar entrada inicial
        animateInitialEntrance();
    }

    private void setupHoverEffects() {
        // Efectos hover para botones principales
        setupButtonHoverEffect(btnLogin);
        setupButtonHoverEffect(btnRegister);
        setupButtonHoverEffect(btnIniciarSesion);
        setupButtonHoverEffect(btnRegistrarse);
    }

    private void setupButtonHoverEffect(Button button) {
        if (button == null) return;

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), button);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        button.setOnMouseEntered(e -> scaleIn.play());
        button.setOnMouseExited(e -> scaleOut.play());

        // Efecto de presión
        button.setOnMousePressed(e -> {
            ScaleTransition press = new ScaleTransition(Duration.millis(100), button);
            press.setToX(0.95);
            press.setToY(0.95);
            press.play();
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition release = new ScaleTransition(Duration.millis(100), button);
            release.setToX(1.05);
            release.setToY(1.05);
            release.play();
        });
    }

    private void animateInitialEntrance() {
        // Animar entrada del formulario de login
        if (loginForm != null) {
            FadeTransition mainFade = new FadeTransition(Duration.millis(1000), loginForm);
            mainFade.setFromValue(0);
            mainFade.setToValue(1);

            ScaleTransition mainScale = new ScaleTransition(Duration.millis(1000), loginForm);
            mainScale.setFromX(0.8);
            mainScale.setFromY(0.8);
            mainScale.setToX(1.0);
            mainScale.setToY(1.0);

            ParallelTransition mainEntrance = new ParallelTransition(mainFade, mainScale);
            mainEntrance.setInterpolator(Interpolator.EASE_OUT);
            mainEntrance.play();
        }
    }

    @FXML
    private void switchToLogin() {
        showLoginForm();
        updateTabButtons(true);
        animateFormTransition(loginForm, registerForm, true);
    }

    @FXML
    private void switchToRegister() {
        showRegisterForm();
        updateTabButtons(false);
        animateFormTransition(registerForm, loginForm, false);
    }

    private void animateFormTransition(VBox showForm, VBox hideForm, boolean isLogin) {
        if (showForm == null || hideForm == null) return;

        // Animación de salida del formulario actual
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), hideForm);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), hideForm);
        slideOut.setFromX(0);
        slideOut.setToX(isLogin ? 50 : -50);

        ParallelTransition exitTransition = new ParallelTransition(fadeOut, slideOut);

        // Animación de entrada del nuevo formulario
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), showForm);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), showForm);
        slideIn.setFromX(isLogin ? -50 : 50);
        slideIn.setToX(0);

        ParallelTransition enterTransition = new ParallelTransition(fadeIn, slideIn);
        enterTransition.setInterpolator(Interpolator.EASE_OUT);

        // Secuencia de animaciones
        exitTransition.setOnFinished(e -> {
            hideForm.setVisible(false);
            showForm.setVisible(true);
            enterTransition.play();
        });

        exitTransition.play();
    }

    @FXML
    private void handleLogin() throws IOException {
        // Animación de loading en el botón
        animateButtonLoading(btnLogin, "Iniciando...");

        String email = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();
        boolean recordarme = chkRecordarme.isSelected();

        // Validaciones básicas
        if (email.isEmpty() || contrasena.isEmpty()) {
            resetButtonAnimation(btnLogin, "Iniciar Sesión");
            showAlert("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        // Validar formato de email
        if (!isValidEmail(email)) {
            resetButtonAnimation(btnLogin, "Iniciar Sesión");
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

                // Animación de éxito
                animateSuccessLogin();

                // Limpiar campos
                clearLoginFields();

                // Navegar a la pantalla de películas con delay
                Timeline delayNavigation = new Timeline(new KeyFrame(Duration.millis(1500), e -> {
                    try {
                        navigateToMovieListing();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }));
                delayNavigation.play();

            } else {
                // Credenciales incorrectas
                resetButtonAnimation(btnLogin, "Iniciar Sesión");
                animateFieldError(txtUsuario);
                animateFieldError(txtContrasena);
                showAlert("Error de Autenticación", "Email o contraseña incorrectos", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            resetButtonAnimation(btnLogin, "Iniciar Sesión");
            System.err.println("Error durante el login: " + e.getMessage());
            showAlert("Error del Sistema", "Ocurrió un error durante el inicio de sesión. Intenta nuevamente.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegister() {
        // Animación de loading en el botón
        animateButtonLoading(btnRegister, "Registrando...");

        String nombreCompleto = txtNombreCompleto.getText().trim();
        String email = txtEmailRegistro.getText().trim();
        String usuario = txtUsuarioRegistro.getText().trim();
        String contrasena = txtContrasenaRegistro.getText();
        String confirmarContrasena = txtConfirmarContrasena.getText();

        // Validaciones básicas
        if (nombreCompleto.isEmpty() || email.isEmpty() || usuario.isEmpty() ||
                contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            resetButtonAnimation(btnRegister, "Registrarse");
            showAlert("Error", "Por favor completa todos los campos", Alert.AlertType.ERROR);
            return;
        }

        // Validar formato de email
        if (!isValidEmail(email)) {
            resetButtonAnimation(btnRegister, "Registrarse");
            animateFieldError(txtEmailRegistro);
            showAlert("Error", "Por favor ingresa un email válido", Alert.AlertType.ERROR);
            return;
        }

        // Validar que las contraseñas coincidan
        if (!contrasena.equals(confirmarContrasena)) {
            resetButtonAnimation(btnRegister, "Registrarse");
            animateFieldError(txtContrasenaRegistro);
            animateFieldError(txtConfirmarContrasena);
            showAlert("Error", "Las contraseñas no coinciden", Alert.AlertType.ERROR);
            return;
        }

        // Validar longitud de contraseña
        if (contrasena.length() < 6) {
            resetButtonAnimation(btnRegister, "Registrarse");
            animateFieldError(txtContrasenaRegistro);
            showAlert("Error", "La contraseña debe tener al menos 6 caracteres", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Verificar si el email ya existe
            Usuario usuarioExistente = usuarioDAO.buscarPorEmail(email);
            if (usuarioExistente != null) {
                resetButtonAnimation(btnRegister, "Registrarse");
                animateFieldError(txtEmailRegistro);
                showAlert("Error", "Ya existe un usuario registrado con este email", Alert.AlertType.ERROR);
                return;
            }

            // Separar nombre y apellido
            String[] partesNombre = nombreCompleto.split("\\s+", 2);
            String nombre = partesNombre[0];
            String apellido = partesNombre.length > 1 ? partesNombre[1] : "";

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario(nombre, apellido, email, contrasena, usuario);

            // Insertar usuario en la base de datos
            boolean registroExitoso = usuarioDAO.insertar(nuevoUsuario);

            if (registroExitoso) {
                // Animación de éxito
                animateSuccessRegister();

                showAlert("Éxito", "Usuario registrado correctamente", Alert.AlertType.INFORMATION);

                System.out.println("Registro exitoso - Usuario: " + nombre + " " + apellido);
                System.out.println("Email: " + email);

                // Limpiar campos del registro
                clearRegisterFields();

                // Cambiar a la vista de login con delay
                Timeline delaySwitch = new Timeline(new KeyFrame(Duration.millis(1000), e -> switchToLogin()));
                delaySwitch.play();

            } else {
                resetButtonAnimation(btnRegister, "Registrarse");
                showAlert("Error", "Error al registrar usuario. Intenta nuevamente.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            resetButtonAnimation(btnRegister, "Registrarse");
            System.err.println("Error durante el registro: " + e.getMessage());
            showAlert("Error del Sistema", "Ocurrió un error durante el registro. Intenta nuevamente.", Alert.AlertType.ERROR);
        }
    }

    private void animateButtonLoading(Button button, String loadingText) {
        if (button == null) return;

        button.setText(loadingText);
        button.setDisable(true);

        // Animación de pulsación
        ScaleTransition pulse = new ScaleTransition(Duration.millis(500), button);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(0.98);
        pulse.setToY(0.98);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();

        // Guardar la animación en el botón para detenerla después
        button.setUserData(pulse);
    }

    private void resetButtonAnimation(Button button, String originalText) {
        if (button == null) return;

        button.setText(originalText);
        button.setDisable(false);

        // Detener animación de pulsación
        Animation pulse = (Animation) button.getUserData();
        if (pulse != null) {
            pulse.stop();
        }

        // Restaurar escala normal
        button.setScaleX(1.0);
        button.setScaleY(1.0);
    }

    private void animateFieldError(Node field) {
        if (field == null) return;

        // Animación de shake para campos con error
        TranslateTransition shake = new TranslateTransition(Duration.millis(500), field);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setInterpolator(Interpolator.LINEAR);
        shake.play();

        // Cambiar temporalmente el borde a rojo
        String originalStyle = field.getStyle();
        field.setStyle(originalStyle + "; -fx-border-color: #ff4444; -fx-border-width: 2;");

        // Restaurar estilo original después de 2 segundos
        Timeline resetStyle = new Timeline(new KeyFrame(Duration.seconds(2), e -> field.setStyle(originalStyle)));
        resetStyle.play();
    }

    private void animateSuccessLogin() {
        if (loginForm == null) return;

        // Animación de éxito para el formulario de login
        ScaleTransition successScale = new ScaleTransition(Duration.millis(300), loginForm);
        successScale.setFromX(1.0);
        successScale.setFromY(1.0);
        successScale.setToX(1.05);
        successScale.setToY(1.05);
        successScale.setAutoReverse(true);
        successScale.setCycleCount(2);
        successScale.play();
    }

    private void animateSuccessRegister() {
        if (registerForm == null) return;

        // Animación de éxito para el formulario de registro
        ScaleTransition successScale = new ScaleTransition(Duration.millis(300), registerForm);
        successScale.setFromX(1.0);
        successScale.setFromY(1.0);
        successScale.setToX(1.05);
        successScale.setToY(1.05);
        successScale.setAutoReverse(true);
        successScale.setCycleCount(2);
        successScale.play();
    }

    @FXML
    private void forgotPassword() {
        if (linkOlvidasteContrasena != null) {
            // Animación sutil para el link
            ScaleTransition linkPulse = new ScaleTransition(Duration.millis(200), linkOlvidasteContrasena);
            linkPulse.setFromX(1.0);
            linkPulse.setFromY(1.0);
            linkPulse.setToX(1.1);
            linkPulse.setToY(1.1);
            linkPulse.setAutoReverse(true);
            linkPulse.setCycleCount(2);
            linkPulse.play();
        }

        System.out.println("Recuperar contraseña");
        showAlert("Información", "Funcionalidad de recuperación de contraseña en desarrollo", Alert.AlertType.INFORMATION);
    }

    private void showLoginForm() {
        if (loginForm != null) loginForm.setVisible(true);
        if (registerForm != null) registerForm.setVisible(false);
    }

    private void showRegisterForm() {
        if (loginForm != null) loginForm.setVisible(false);
        if (registerForm != null) registerForm.setVisible(true);
    }

    private void updateTabButtons(boolean isLogin) {
        if (isLogin) {
            if (btnIniciarSesion != null) {
                btnIniciarSesion.setStyle("-fx-background-color: rgba(255, 255, 255, 0.25); -fx-background-radius: 15 0 0 15; -fx-border-width: 0; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            }
            if (btnRegistrarse != null) {
                btnRegistrarse.setStyle("-fx-background-color: transparent; -fx-background-radius: 0 15 15 0; -fx-border-width: 0; -fx-text-fill: #D0D8E0; -fx-cursor: hand;");
            }
        } else {
            if (btnRegistrarse != null) {
                btnRegistrarse.setStyle("-fx-background-color: rgba(255, 255, 255, 0.25); -fx-background-radius: 0 15 15 0; -fx-border-width: 0; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            }
            if (btnIniciarSesion != null) {
                btnIniciarSesion.setStyle("-fx-background-color: transparent; -fx-background-radius: 15 0 0 15; -fx-border-width: 0; -fx-text-fill: #D0D8E0; -fx-cursor: hand;");
            }
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
        if (txtUsuario != null) txtUsuario.clear();
        if (txtContrasena != null) txtContrasena.clear();
        if (chkRecordarme != null) chkRecordarme.setSelected(false);
    }

    private void clearRegisterFields() {
        if (txtNombreCompleto != null) txtNombreCompleto.clear();
        if (txtEmailRegistro != null) txtEmailRegistro.clear();
        if (txtUsuarioRegistro != null) txtUsuarioRegistro.clear();
        if (txtContrasenaRegistro != null) txtContrasenaRegistro.clear();
        if (txtConfirmarContrasena != null) txtConfirmarContrasena.clear();
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