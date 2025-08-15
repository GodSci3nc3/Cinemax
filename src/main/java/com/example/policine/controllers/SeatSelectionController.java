package com.example.policine.controllers;

import com.example.policine.model.dao.FuncionDAO;
import com.example.policine.model.dao.PeliculaDAO;
import com.example.policine.model.dao.SalaDAO;
import com.example.policine.model.entities.Funcion;
import com.example.policine.model.entities.Pelicula;
import com.example.policine.model.entities.Sala;
import com.example.policine.model.session.BookingSession;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SeatSelectionController implements Initializable {

    @FXML private VBox headerSection;
    @FXML private HBox mainContent;
    @FXML private VBox seatsSection;
    @FXML private VBox seatsContainer;
    @FXML private VBox summarySection;
    @FXML private ImageView logoImage;
    @FXML private Label lblTituloPelicula;
    @FXML private Label lblDetallesFuncion;
    @FXML private Label lblSelectedCount;
    @FXML private GridPane seatsGrid;
    @FXML private VBox selectedSeatsContainer;
    @FXML private ScrollPane selectedSeatsPane;
    @FXML private TextField txtCodigoDescuento;
    @FXML private Button btnApplyDiscount;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblTotal;
    @FXML private Button btnContinuar;
    @FXML private Button btnVolver;

    // Seat management
    private Set<String> selectedSeats;
    private Set<String> occupiedSeats;
    private Map<String, Button> seatButtons;
    private Map<String, Double> seatPrices;

    // Session data
    private BookingSession session;

    // Pricing
    private final double PRECIO_NORMAL = 10.00;
    private final double PRECIO_PREMIUM = 15.00;
    private double descuentoAplicado = 0.0;

    // Animation timelines
    private Timeline pulseAnimation;
    private Timeline logoAnimation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        session = BookingSession.getInstance();
        inicializarColecciones();
        configurarInterfaz();
        inicializarAnimaciones();
    }

    public void initializeWithSessionData() {
        cargarDatosDeSesion();
        actualizarInterfaz();
        generarAsientos();
        animateContentEntry();
    }

    private void inicializarColecciones() {
        selectedSeats = new HashSet<>();
        occupiedSeats = new HashSet<>();
        seatButtons = new HashMap<>();
        seatPrices = new HashMap<>();

        // Simular algunos asientos ocupados (en un caso real, esto vendría de la base de datos)
        occupiedSeats.addAll(Arrays.asList("A1", "A2", "C5", "D10", "F15", "H20"));
    }

    private void configurarInterfaz() {
        btnContinuar.setDisable(true);
        selectedSeatsPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        lblSelectedCount.setText("(0)");

        // Configurar listener para el código de descuento
        txtCodigoDescuento.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(oldVal)) {
                animateDiscountField();
            }
        });

        // Hover effects para botones principales
        configurarHoverEffects();
    }

    private void inicializarAnimaciones() {
        // Logo breathing animation
        logoAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(logoImage.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.ZERO, new KeyValue(logoImage.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.seconds(3), new KeyValue(logoImage.scaleXProperty(), 1.05)),
                new KeyFrame(Duration.seconds(3), new KeyValue(logoImage.scaleYProperty(), 1.05)),
                new KeyFrame(Duration.seconds(6), new KeyValue(logoImage.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.seconds(6), new KeyValue(logoImage.scaleYProperty(), 1.0))
        );
        logoAnimation.setCycleCount(Timeline.INDEFINITE);
        logoAnimation.play();
    }

    private void configurarHoverEffects() {
        // Efecto hover para botón volver
        btnVolver.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btnVolver);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        btnVolver.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btnVolver);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        // Efecto hover para botón continuar
        btnContinuar.setOnMouseEntered(e -> {
            if (!btnContinuar.isDisabled()) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), btnContinuar);
                scale.setToX(1.02);
                scale.setToY(1.02);
                scale.play();
            }
        });

        btnContinuar.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btnContinuar);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }

    private void animateContentEntry() {
        // Animación de entrada para el contenido principal
        mainContent.setOpacity(0);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(800), mainContent);
        slideIn.setFromY(50);
        slideIn.setToY(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ParallelTransition entrance = new ParallelTransition(slideIn, fadeIn);
        entrance.setDelay(Duration.millis(300));
        entrance.play();

        // Animación escalonada para los asientos
        animateSeatsEntry();
    }

    private void animateSeatsEntry() {
        Timeline staggerAnimation = new Timeline();
        int delay = 0;

        for (int fila = 0; fila < 10; fila++) {
            for (int columna = 0; columna < 10; columna++) {
                char letraFila = (char) ('A' + fila);
                String seatId = letraFila + String.valueOf(columna + 1);
                Button seatButton = seatButtons.get(seatId);

                if (seatButton != null) {
                    seatButton.setScaleX(0);
                    seatButton.setScaleY(0);

                    KeyFrame keyFrame = new KeyFrame(
                            Duration.millis(delay),
                            e -> {
                                ScaleTransition scale = new ScaleTransition(Duration.millis(300), seatButton);
                                scale.setFromX(0);
                                scale.setFromY(0);
                                scale.setToX(1);
                                scale.setToY(1);
                                scale.setInterpolator(Interpolator.EASE_OUT);
                                scale.play();
                            }
                    );
                    staggerAnimation.getKeyFrames().add(keyFrame);
                    delay += 20; // Retraso entre asientos
                }
            }
        }

        staggerAnimation.setDelay(Duration.millis(500));
        staggerAnimation.play();
    }

    private void animateDiscountField() {
        // Animación sutil cuando se escribe en el campo de descuento
        ScaleTransition pulse = new ScaleTransition(Duration.millis(150), txtCodigoDescuento);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.02);
        pulse.setToY(1.02);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.play();
    }

    private void cargarDatosDeSesion() {
        // Los datos ya están en la sesión, no necesitamos cargar nada adicional
    }

    private void actualizarInterfaz() {
        Pelicula pelicula = session.getPelicula();
        Funcion funcion = session.getFuncion();
        Sala sala = session.getSala();

        if (pelicula != null) {
            lblTituloPelicula.setText(pelicula.getTitulo());
        }

        if (funcion != null && sala != null) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String detalles = String.format("%s • %s • %s",
                    funcion.getHora().format(timeFormatter),
                    sala.getNombreSala(),
                    funcion.getFecha().format(dateFormatter)
            );
            lblDetallesFuncion.setText(detalles);
        }
    }

    private void generarAsientos() {
        seatsGrid.getChildren().clear();
        seatButtons.clear();
        seatPrices.clear();

        // Configurar el grid para 10 filas (A-J) y 10 columnas
        int filas = 10;
        int columnas = 10;

        char[] letrasFila = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};

        for (int fila = 0; fila < filas; fila++) {
            for (int columna = 0; columna < columnas; columna++) {
                String seatId = letrasFila[fila] + String.valueOf(columna + 1);

                Button seatButton = crearBotonAsiento(seatId, fila, columna);
                seatButtons.put(seatId, seatButton);

                // Añadir al grid
                seatsGrid.add(seatButton, columna, fila);
            }
        }
    }

    private Button crearBotonAsiento(String seatId, int fila, int columna) {
        Button button = new Button(String.valueOf(columna + 1));
        button.setPrefSize(38, 38);
        button.setFont(Font.font("System", FontWeight.BOLD, 11));

        // Determinar tipo de asiento y precio
        double precio = PRECIO_NORMAL;
        String tipoAsiento;

        // Filas premium (últimas 3 filas)
        if (fila >= 7) {
            precio = PRECIO_PREMIUM;
            tipoAsiento = "premium";
        } else {
            tipoAsiento = "normal";
        }

        seatPrices.put(seatId, precio);

        // Configurar estilo según estado
        if (occupiedSeats.contains(seatId)) {
            configurarAsientoOcupado(button);
        } else {
            configurarAsientoDisponible(button, tipoAsiento);
            configurarAnimacionesAsiento(button);
        }

        // Configurar acción del botón
        button.setOnAction(e -> toggleSeatSelection(seatId, button, tipoAsiento));

        return button;
    }

    private void configurarAnimacionesAsiento(Button button) {
        // Hover effect para asientos disponibles
        button.setOnMouseEntered(e -> {
            if (!button.isDisabled()) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
                scale.setToX(1.15);
                scale.setToY(1.15);
                scale.play();
            }
        });

        button.setOnMouseExited(e -> {
            if (!button.isDisabled()) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            }
        });
    }

    private void configurarAsientoDisponible(Button button, String tipo) {
        String color = tipo.equals("premium") ? "#f39c12" : "#4a90e2";
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1; -fx-border-radius: 5;",
                color
        ));
        button.setDisable(false);
    }

    private void configurarAsientoOcupado(Button button) {
        button.setStyle("-fx-background-color: #666666; -fx-text-fill: #999; -fx-background-radius: 5; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 1; -fx-border-radius: 5;");
        button.setDisable(true);
    }

    private void configurarAsientoSeleccionado(Button button, String tipo) {
        button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-color: rgba(255,255,255,0.2); -fx-border-width: 1; -fx-border-radius: 5;");
    }

    private void toggleSeatSelection(String seatId, Button button, String tipo) {
        if (selectedSeats.contains(seatId)) {
            // Deseleccionar con animación
            selectedSeats.remove(seatId);
            configurarAsientoDisponible(button, tipo);

            // Animación de deselección
            ScaleTransition deselect = new ScaleTransition(Duration.millis(200), button);
            deselect.setFromX(1.2);
            deselect.setFromY(1.2);
            deselect.setToX(1.0);
            deselect.setToY(1.0);
            deselect.play();
        } else {
            // Seleccionar con animación
            selectedSeats.add(seatId);
            configurarAsientoSeleccionado(button, tipo);

            // Animación de selección
            ScaleTransition select = new ScaleTransition(Duration.millis(200), button);
            select.setFromX(1.0);
            select.setFromY(1.0);
            select.setToX(1.2);
            select.setToY(1.2);
            select.setAutoReverse(true);
            select.setCycleCount(2);
            select.play();
        }

        actualizarResumenSeleccion();
        actualizarPrecios();
    }

    private void actualizarResumenSeleccion() {
        selectedSeatsContainer.getChildren().clear();
        lblSelectedCount.setText("(" + selectedSeats.size() + ")");

        List<String> asientosOrdenados = new ArrayList<>(selectedSeats);
        Collections.sort(asientosOrdenados);

        for (int i = 0; i < asientosOrdenados.size(); i++) {
            String seatId = asientosOrdenados.get(i);
            HBox seatItem = crearItemAsientoSeleccionado(seatId);

            // Animación de entrada para cada item
            seatItem.setOpacity(0);
            seatItem.setTranslateX(-20);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), seatItem);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), seatItem);
            slideIn.setFromX(-20);
            slideIn.setToX(0);

            ParallelTransition entrance = new ParallelTransition(fadeIn, slideIn);
            entrance.setDelay(Duration.millis(i * 100)); // Efecto escalonado

            selectedSeatsContainer.getChildren().add(seatItem);
            entrance.play();
        }

        btnContinuar.setDisable(selectedSeats.isEmpty());

        // Animación del botón continuar cuando se activa
        if (!selectedSeats.isEmpty() && btnContinuar.isDisabled() == false) {
            animateContinueButton();
        }
    }

    private void animateContinueButton() {
        // Pulso sutil en el botón continuar
        ScaleTransition pulse = new ScaleTransition(Duration.millis(300), btnContinuar);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.play();
    }

    private HBox crearItemAsientoSeleccionado(String seatId) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 8; -fx-padding: 12; -fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1; -fx-border-radius: 8;");

        Label lblAsiento = new Label("Asiento " + seatId);
        lblAsiento.setTextFill(Color.WHITE);
        lblAsiento.setFont(Font.font("System", FontWeight.NORMAL, 13));

        // Determinar si es premium
        boolean isPremium = seatPrices.get(seatId) == PRECIO_PREMIUM;
        if (isPremium) {
            lblAsiento.setText("Asiento " + seatId + " (Premium)");
            lblAsiento.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
        }

        Label lblPrecio = new Label(String.format("$%.2f", seatPrices.get(seatId)));
        lblPrecio.setTextFill(Color.WHITE);
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 13));

        Button btnRemover = new Button("✕");
        btnRemover.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 15; -fx-min-width: 25; -fx-min-height: 25; -fx-max-width: 25; -fx-max-height: 25;");

        // Hover effect para botón remover
        btnRemover.setOnMouseEntered(e -> {
            btnRemover.setStyle("-fx-background-color: rgba(231,76,60,0.2); -fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 15; -fx-min-width: 25; -fx-min-height: 25; -fx-max-width: 25; -fx-max-height: 25;");
        });

        btnRemover.setOnMouseExited(e -> {
            btnRemover.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 15; -fx-min-width: 25; -fx-min-height: 25; -fx-max-width: 25; -fx-max-height: 25;");
        });

        btnRemover.setOnAction(e -> {
            Button seatButton = seatButtons.get(seatId);
            String tipo = seatPrices.get(seatId) == PRECIO_PREMIUM ? "premium" : "normal";

            // Animación de salida del item
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), item);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), item);
            slideOut.setFromX(0);
            slideOut.setToX(-50);

            ParallelTransition exit = new ParallelTransition(fadeOut, slideOut);
            exit.setOnFinished(ev -> toggleSeatSelection(seatId, seatButton, tipo));
            exit.play();
        });

        // Spacer
        Label spacer = new Label();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        item.getChildren().addAll(lblAsiento, spacer, lblPrecio, btnRemover);
        return item;
    }

    private void actualizarPrecios() {
        double subtotal = selectedSeats.stream()
                .mapToDouble(seat -> seatPrices.get(seat))
                .sum();

        double total = subtotal - descuentoAplicado;

        // Animación sutil en los precios cuando cambian
        animateLabel(lblSubtotal, String.format("$%.2f", subtotal));
        animateLabel(lblDescuento, String.format("-$%.2f", descuentoAplicado));
        animateLabel(lblTotal, String.format("$%.2f", total));
    }

    private void animateLabel(Label label, String newText) {
        if (!label.getText().equals(newText)) {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), label);
            scale.setFromX(1.0);
            scale.setFromY(1.0);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.setAutoReverse(true);
            scale.setCycleCount(2);
            scale.setOnFinished(e -> label.setText(newText));
            scale.play();
        }
    }

    @FXML
    private void aplicarDescuentoManual() {
        aplicarDescuento();

        // Feedback visual
        ScaleTransition feedback = new ScaleTransition(Duration.millis(150), btnApplyDiscount);
        feedback.setFromX(1.0);
        feedback.setFromY(1.0);
        feedback.setToX(0.95);
        feedback.setToY(0.95);
        feedback.setAutoReverse(true);
        feedback.setCycleCount(2);
        feedback.play();
    }

    private void aplicarDescuento() {
        String codigo = txtCodigoDescuento.getText().trim().toLowerCase();
        double descuentoAnterior = descuentoAplicado;
        descuentoAplicado = 0.0;

        // Códigos de descuento de ejemplo
        switch (codigo) {
            case "desc10":
                descuentoAplicado = calcularSubtotal() * 0.10;
                mostrarMensajeDescuento("Descuento del 10% aplicado", true);
                break;
            case "desc20":
                descuentoAplicado = calcularSubtotal() * 0.20;
                mostrarMensajeDescuento("Descuento del 20% aplicado", true);
                break;
            case "estudiante":
                descuentoAplicado = calcularSubtotal() * 0.15;
                mostrarMensajeDescuento("Descuento estudiantil aplicado", true);
                break;
            default:
                if (!codigo.isEmpty()) {
                    mostrarMensajeDescuento("Código de descuento inválido", false);
                }
                break;
        }

        // Solo actualizar si el descuento cambió
        if (descuentoAnterior != descuentoAplicado) {
            actualizarPrecios();
            session.setDescuento(descuentoAplicado);
        }
    }

    private void mostrarMensajeDescuento(String mensaje, boolean esExito) {
        Label mensajeLabel = new Label(mensaje);
        mensajeLabel.setStyle(esExito ?
                "-fx-text-fill: #27ae60; -fx-font-size: 11px; -fx-font-weight: bold;" :
                "-fx-text-fill: #e74c3c; -fx-font-size: 11px; -fx-font-weight: bold;");

        // Buscar el contenedor padre del campo de descuento y agregar el mensaje temporalmente
        VBox parentContainer = (VBox) txtCodigoDescuento.getParent().getParent();
        parentContainer.getChildren().add(mensajeLabel);

        // Animación de entrada y salida
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), mensajeLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        Timeline removeMessage = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), mensajeLabel);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev -> parentContainer.getChildren().remove(mensajeLabel));
            fadeOut.play();
        }));

        fadeIn.play();
        removeMessage.play();
    }

    private double calcularSubtotal() {
        return selectedSeats.stream()
                .mapToDouble(seat -> seatPrices.get(seat))
                .sum();
    }

    @FXML
    private void volverAtras() {
        try {
            // Animación de salida
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), mainContent);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);

            fadeOut.setOnFinished(e -> {
                try {
                    // Limpiar datos de asientos de la sesión
                    session.setSeatData(new HashSet<>(), new HashMap<>());

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/movieListing.fxml"));
                    Parent movieListingRoot = loader.load();

                    Stage currentStage = (Stage) btnVolver.getScene().getWindow();
                    Scene movieListingScene = new Scene(movieListingRoot);
                    currentStage.setScene(movieListingScene);
                    currentStage.setTitle("Cinemax - Cartelera");
                    currentStage.centerOnScreen();

                } catch (IOException ex) {
                    System.err.println("Error al volver a movie listing: " + ex.getMessage());
                    mostrarError("Error al cargar la pantalla anterior");
                }
            });

            fadeOut.play();

        } catch (Exception ex) {
            System.err.println("Error en animación de salida: " + ex.getMessage());
            // Fallback sin animación
            try {
                session.setSeatData(new HashSet<>(), new HashMap<>());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/movieListing.fxml"));
                Parent movieListingRoot = loader.load();
                Stage currentStage = (Stage) btnVolver.getScene().getWindow();
                Scene movieListingScene = new Scene(movieListingRoot);
                currentStage.setScene(movieListingScene);
                currentStage.setTitle("Cinemax - Cartelera");
                currentStage.centerOnScreen();
            } catch (IOException e) {
                mostrarError("Error al cargar la pantalla anterior");
            }
        }
    }

    @FXML
    private void continuarAlCarrito() throws IOException {
        if (selectedSeats.isEmpty()) {
            mostrarAlerta("Debe seleccionar al menos un asiento");
            return;
        }

        // Animación de éxito antes de continuar
        ScaleTransition success = new ScaleTransition(Duration.millis(200), btnContinuar);
        success.setFromX(1.0);
        success.setFromY(1.0);
        success.setToX(1.1);
        success.setToY(1.1);
        success.setAutoReverse(true);
        success.setCycleCount(2);

        success.setOnFinished(e -> {
            try {
                // Guardar datos de asientos en la sesión
                session.setSeatData(selectedSeats, seatPrices);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/foodSelection.fxml"));
                Parent foodSelectionRoot = loader.load();

                // Obtener el controlador y pasarle los datos
                FoodSelectionController controller = loader.getController();
                controller.initializeWithSessionData();

                Stage currentStage = (Stage) btnContinuar.getScene().getWindow();
                Scene foodSelectionScene = new Scene(foodSelectionRoot);
                currentStage.setScene(foodSelectionScene);
                currentStage.setTitle("Cinemax - Comida y Bebidas");
                currentStage.centerOnScreen();

                System.out.println("Navegación exitosa a Food Selection");

            } catch (IOException ex) {
                System.err.println("Error al navegar a food selection: " + ex.getMessage());
                mostrarError("Error al cargar la siguiente pantalla");
            }
        });

        success.play();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Personalizar el estilo del diálogo
        alert.getDialogPane().setStyle("-fx-background-color: #2c3e50;");
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Personalizar el estilo del diálogo
        alert.getDialogPane().setStyle("-fx-background-color: #2c3e50;");
        alert.showAndWait();
    }
}