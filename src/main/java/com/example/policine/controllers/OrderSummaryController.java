package com.example.policine.controllers;

import com.example.policine.model.session.BookingSession;
import com.example.policine.model.entities.Pelicula;
import com.example.policine.model.entities.Funcion;
import com.example.policine.model.entities.Sala;
import com.example.policine.services.BookingService;
import com.example.policine.services.BookingService.ReservaCompleta;
import com.example.policine.services.BookingService.ReservaException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class OrderSummaryController implements Initializable {

    @FXML private Button backButton;
    @FXML private VBox orderItemsContainer;
    @FXML private Label totalLabel;
    @FXML private Label bottomTotalLabel;
    @FXML private RadioButton pickupRadio;
    @FXML private RadioButton deliveryRadio;
    @FXML private ToggleGroup deliveryGroup;
    @FXML private Button confirmButton;
    @FXML private Label timerLabel;
    @FXML private Label cashAmountLabel;

    // Session data
    private BookingSession session;
    private BookingService bookingService;

    // Timer para la sesión
    private Timer sessionTimer;
    private int remainingSeconds = 600; // 10 minutos = 600 segundos

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        session = BookingSession.getInstance();
        bookingService = new BookingService();
        setupRadioButtons();
        startSessionTimer();
    }

    public void initializeWithSessionData() {
        loadOrderSummary();
        updateTotalLabels();
        // Verificar disponibilidad de asientos antes de mostrar
        verificarDisponibilidadAsientos();
    }

    private void verificarDisponibilidadAsientos() {
        if (session.getFuncion() == null || session.getAsientosSeleccionados().isEmpty()) {
            return;
        }

        // Verificar en segundo plano para no bloquear UI
        new Thread(() -> {
            boolean disponibles = bookingService.verificarDisponibilidadAsientos(
                    session.getFuncion().getIdFuncion(),
                    session.getAsientosSeleccionados()
            );

            if (!disponibles) {
                Platform.runLater(() -> {
                    showAsientosNoDisponiblesAlert();
                });
            }
        }).start();
    }

    private void showAsientosNoDisponiblesAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Asientos No Disponibles");
        alert.setHeaderText("Algunos asientos ya no están disponibles");
        alert.setContentText("Los asientos que seleccionaste han sido reservados por otro usuario. " +
                "Debes seleccionar nuevos asientos.");

        alert.setOnHidden(e -> {
            try {
                volverASeleccionAsientos();
            } catch (IOException ex) {
                System.err.println("Error al volver a selección de asientos: " + ex.getMessage());
            }
        });

        alert.showAndWait();
    }

    private void startSessionTimer() {
        sessionTimer = new Timer();
        sessionTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    remainingSeconds--;
                    updateTimerDisplay();

                    if (remainingSeconds <= 0) {
                        sessionExpired();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void updateTimerDisplay() {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        String timeText = String.format("Tiempo restante: %02d:%02d", minutes, seconds);
        timerLabel.setText(timeText);

        // Cambiar color cuando quede poco tiempo
        if (remainingSeconds <= 120) { // 2 minutos
            timerLabel.setTextFill(Color.web("#ff6b35"));
        } else if (remainingSeconds <= 300) { // 5 minutos
            timerLabel.setTextFill(Color.web("#ffcc00"));
        } else {
            timerLabel.setTextFill(Color.web("#8b9dc3"));
        }
    }

    private void sessionExpired() {
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sesión Expirada");
            alert.setHeaderText("Tiempo agotado");
            alert.setContentText("Tu sesión ha expirado por inactividad. Debes reiniciar el proceso de compra.");

            alert.setOnHidden(e -> {
                session.reset();
                try {
                    returnToMovieListing();
                } catch (IOException ex) {
                    System.err.println("Error al volver a movie listing: " + ex.getMessage());
                }
            });

            alert.showAndWait();
        });
    }

    private void loadOrderSummary() {
        orderItemsContainer.getChildren().clear();

        // Información de la película y función
        addMovieInfo();

        // Información de asientos
        addSeatsInfo();

        // Información de comida (si hay)
        addFoodInfo();

        // Separador y totales
        addTotalsSection();
    }

    private void addMovieInfo() {
        Pelicula pelicula = session.getPelicula();
        Funcion funcion = session.getFuncion();
        Sala sala = session.getSala();

        if (pelicula == null || funcion == null || sala == null) return;

        VBox movieSection = new VBox(5);
        movieSection.setStyle("-fx-padding: 0 0 10 0;");

        Label movieTitle = new Label("🎬 " + pelicula.getTitulo());
        movieTitle.setTextFill(Color.WHITE);
        movieTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Label functionDetails = new Label(String.format("%s • %s • %s",
                funcion.getHora().format(timeFormatter),
                sala.getNombreSala(),
                funcion.getFecha().format(dateFormatter)));
        functionDetails.setTextFill(Color.LIGHTGRAY);
        functionDetails.setFont(Font.font("System", 12));

        movieSection.getChildren().addAll(movieTitle, functionDetails);
        orderItemsContainer.getChildren().add(movieSection);
    }

    private void addSeatsInfo() {
        if (session.getAsientosSeleccionados().isEmpty()) return;

        VBox seatsSection = new VBox(8);
        seatsSection.setStyle("-fx-padding: 10 0;");

        Label seatsHeader = new Label("🪑 Asientos Seleccionados");
        seatsHeader.setTextFill(Color.WHITE);
        seatsHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
        seatsSection.getChildren().add(seatsHeader);

        for (String asiento : session.getAsientosSeleccionados()) {
            HBox seatItem = createOrderItem(
                    "Asiento " + asiento,
                    "1 x $" + String.format("%.2f", session.getPreciosAsientos().get(asiento)),
                    "$" + String.format("%.2f", session.getPreciosAsientos().get(asiento))
            );
            seatsSection.getChildren().add(seatItem);
        }

        orderItemsContainer.getChildren().add(seatsSection);
    }

    private void addFoodInfo() {
        Map<String, Integer> comida = session.getComidaSeleccionada();
        if (comida.isEmpty()) return;

        VBox foodSection = new VBox(8);
        foodSection.setStyle("-fx-padding: 10 0;");

        Label foodHeader = new Label("🍿 Comida y Bebidas");
        foodHeader.setTextFill(Color.WHITE);
        foodHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
        foodSection.getChildren().add(foodHeader);

        for (Map.Entry<String, Integer> entry : comida.entrySet()) {
            String producto = entry.getKey();
            int cantidad = entry.getValue();
            double precioUnitario = session.getPreciosComida().get(producto);
            double precioTotal = precioUnitario * cantidad;

            HBox foodItem = createOrderItem(
                    producto,
                    cantidad + " x $" + String.format("%.2f", precioUnitario),
                    "$" + String.format("%.2f", precioTotal)
            );
            foodSection.getChildren().add(foodItem);
        }

        orderItemsContainer.getChildren().add(foodSection);
    }

    private void addTotalsSection() {
        VBox totalsSection = new VBox(8);
        totalsSection.setStyle("-fx-padding: 10 0 0 0; -fx-border-color: #3a4258; -fx-border-width: 1 0 0 0;");

        // Subtotal
        HBox subtotalItem = createTotalItem("Subtotal",
                "$" + String.format("%.2f", session.getSubtotalGeneral()), false);
        totalsSection.getChildren().add(subtotalItem);

        // Impuestos
        HBox taxItem = createTotalItem("Impuestos",
                "$" + String.format("%.2f", session.getImpuestos()), false);
        totalsSection.getChildren().add(taxItem);

        // Descuento (si hay)
        if (session.getDescuento() > 0) {
            HBox discountItem = createTotalItem("Descuento",
                    "-$" + String.format("%.2f", session.getDescuento()), false);
            totalsSection.getChildren().add(discountItem);
        }

        // Total
        HBox totalItem = createTotalItem("Total",
                "$" + String.format("%.2f", session.getTotal()), true);
        totalsSection.getChildren().add(totalItem);

        orderItemsContainer.getChildren().add(totalsSection);
    }

    private HBox createOrderItem(String nombre, String detalle, String precio) {
        HBox item = new HBox();
        item.setAlignment(Pos.CENTER_LEFT);
        item.setSpacing(10);

        VBox infoBox = new VBox(2);

        Label nombreLabel = new Label(nombre);
        nombreLabel.setTextFill(Color.WHITE);
        nombreLabel.setFont(Font.font("System", 16));

        Label detalleLabel = new Label(detalle);
        detalleLabel.setTextFill(Color.LIGHTGRAY);
        detalleLabel.setFont(Font.font("System", 14));

        infoBox.getChildren().addAll(nombreLabel, detalleLabel);

        Label precioLabel = new Label(precio);
        precioLabel.setTextFill(Color.WHITE);
        precioLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Spacer
        Label spacer = new Label();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        item.getChildren().addAll(infoBox, spacer, precioLabel);
        return item;
    }

    private HBox createTotalItem(String label, String value, boolean isTotal) {
        HBox item = new HBox();
        item.setAlignment(Pos.CENTER_LEFT);

        Label labelText = new Label(label);
        labelText.setTextFill(isTotal ? Color.WHITE : Color.LIGHTGRAY);
        labelText.setFont(Font.font("System", isTotal ? FontWeight.BOLD : FontWeight.NORMAL,
                isTotal ? 18 : 16));

        Label valueText = new Label(value);
        valueText.setTextFill(isTotal ? Color.web("#ff6b35") : Color.LIGHTGRAY);
        valueText.setFont(Font.font("System", FontWeight.BOLD, isTotal ? 18 : 16));

        // Spacer
        Label spacer = new Label();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        item.getChildren().addAll(labelText, spacer, valueText);
        return item;
    }

    private void setupRadioButtons() {
        deliveryGroup = new ToggleGroup();
        pickupRadio.setToggleGroup(deliveryGroup);
        deliveryRadio.setToggleGroup(deliveryGroup);
        pickupRadio.setSelected(true);

        deliveryGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                RadioButton selected = (RadioButton) newValue;
                boolean entregarEnAsiento = selected == deliveryRadio;
                session.setEntregarEnAsiento(entregarEnAsiento);
                System.out.println("Opción de entrega seleccionada: " + selected.getText());
            }
        });
    }

    private void updateTotalLabels() {
        String totalText = String.format("$%.2f", session.getTotal());
        totalLabel.setText(totalText);
        bottomTotalLabel.setText("Total: " + totalText);
        cashAmountLabel.setText(totalText);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/foodSelection.fxml"));
            Parent foodSelectionRoot = loader.load();

            // Obtener el controlador y reinicializarlo
            FoodSelectionController controller = loader.getController();
            controller.initializeWithSessionData();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene foodSelectionScene = new Scene(foodSelectionRoot);
            stage.setScene(foodSelectionScene);
            stage.setTitle("Cinemax - Comida y Bebidas");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Error al volver a food selection: " + e.getMessage());
            showAlert("Error", "Error al cargar la pantalla anterior", Alert.AlertType.ERROR);
        }
    }

    private void volverASeleccionAsientos() throws IOException {
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/seatSelection.fxml"));
        Parent seatSelectionRoot = loader.load();

        // Reinicializar controlador de selección de asientos
        SeatSelectionController controller = loader.getController();
        controller.initializeWithSessionData();

        Stage stage = (Stage) confirmButton.getScene().getWindow();
        Scene seatSelectionScene = new Scene(seatSelectionRoot);
        stage.setScene(seatSelectionScene);
        stage.setTitle("Cinemax - Selección de Asientos");
        stage.centerOnScreen();
    }

    @FXML
    private void handleConfirmOrder(ActionEvent event) {
        processOrder();
    }

    private void processOrder() {
        System.out.println("Procesando pedido...");
        System.out.println(session.getResumenCompleto());

        confirmButton.setDisable(true);
        confirmButton.setText("Procesando...");

        // Detener el timer
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }

        // Procesar en hilo separado
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Simular procesamiento

                // Usar el servicio mejorado para guardar la reserva
                ReservaCompleta reservaCompleta = bookingService.procesarReserva(session);

                Platform.runLater(() -> {
                    showSuccessAlert(reservaCompleta);
                });

            } catch (ReservaException e) {
                Platform.runLater(() -> {
                    showReservaErrorAlert(e.getMessage());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showErrorAlert();
                });
            }
        }).start();
    }

    private void showReservaErrorAlert(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error en la Reserva");
        alert.setHeaderText("No se pudo completar la reserva");
        alert.setContentText(mensaje + "\n\nPor favor, intenta nuevamente.");

        alert.setOnHidden(e -> {
            confirmButton.setDisable(false);
            confirmButton.setText("Confirmar Compra");
            startSessionTimer(); // Reiniciar timer
        });

        alert.showAndWait();
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error en la Compra");
        alert.setHeaderText("No se pudo procesar tu reserva");
        alert.setContentText("Ha ocurrido un error técnico. Por favor, intenta nuevamente.");

        alert.setOnHidden(e -> {
            confirmButton.setDisable(false);
            confirmButton.setText("Confirmar Compra");
            startSessionTimer(); // Reiniciar timer
        });

        alert.showAndWait();
    }

    private void showSuccessAlert(ReservaCompleta reservaCompleta) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Reserva Confirmada!");
        alert.setHeaderText("Tu reserva ha sido procesada exitosamente");

        String mensaje = String.format(
                "Número de Reserva: #%d\n" +
                        "Asientos: %s\n" +
                        "Total: $%.2f\n\n" +
                        "Deberás realizar el pago en taquilla antes del inicio de la función.\n" +
                        "Por favor, llega con al menos 30 minutos de anticipación.\n\n" +
                        "Recuerda llevar efectivo exacto para facilitar el proceso.",
                reservaCompleta.getReserva().getIdReserva(),
                String.join(", ", reservaCompleta.getCodigosAsientos()),
                reservaCompleta.getTotal()
        );

        alert.setContentText(mensaje);

        alert.setOnHidden(e -> {
            // Limpiar sesión y volver al inicio
            session.reset();
            try {
                returnToMovieListing();
            } catch (IOException ex) {
                System.err.println("Error al volver a movie listing: " + ex.getMessage());
            }
        });

        alert.showAndWait();
    }

    private void returnToMovieListing() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/movieListing.fxml"));
        Parent movieListingRoot = loader.load();

        Stage currentStage = (Stage) confirmButton.getScene().getWindow();
        Scene movieListingScene = new Scene(movieListingRoot);
        currentStage.setScene(movieListingScene);
        currentStage.setTitle("Cinemax - Cartelera");
        currentStage.centerOnScreen();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}