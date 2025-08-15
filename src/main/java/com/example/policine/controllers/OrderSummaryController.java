package com.example.policine.controllers;

import com.example.policine.model.session.BookingSession;
import com.example.policine.model.entities.Pelicula;
import com.example.policine.model.entities.Funcion;
import com.example.policine.model.entities.Sala;
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

public class OrderSummaryController implements Initializable {

    @FXML private Button backButton;
    @FXML private VBox orderItemsContainer;
    @FXML private Label totalLabel;
    @FXML private Label bottomTotalLabel;
    @FXML private RadioButton pickupRadio;
    @FXML private RadioButton deliveryRadio;
    @FXML private ToggleGroup deliveryGroup;
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvcField;
    @FXML private TextField cardHolderField;
    @FXML private Button confirmButton;

    // Session data
    private BookingSession session;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        session = BookingSession.getInstance();
        setupRadioButtons();
        setupTextFieldValidations();
    }

    public void initializeWithSessionData() {
        loadOrderSummary();
        updateTotalLabels();
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

    private void setupTextFieldValidations() {
        // Validación para el número de tarjeta
        cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d\\s]*")) {
                cardNumberField.setText(newValue.replaceAll("[^\\d\\s]", ""));
            }
            if (newValue.length() <= 19) {
                String formatted = formatCardNumber(newValue.replaceAll("\\s", ""));
                if (!formatted.equals(newValue)) {
                    Platform.runLater(() -> {
                        cardNumberField.setText(formatted);
                        cardNumberField.positionCaret(formatted.length());
                    });
                }
            }
        });

        // Validación para fecha de vencimiento
        expiryField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d/]*")) {
                expiryField.setText(newValue.replaceAll("[^\\d/]", ""));
            }
            if (newValue.length() <= 5) {
                String formatted = formatExpiryDate(newValue.replaceAll("/", ""));
                if (!formatted.equals(newValue)) {
                    Platform.runLater(() -> {
                        expiryField.setText(formatted);
                        expiryField.positionCaret(formatted.length());
                    });
                }
            }
        });

        // Validación para CVC
        cvcField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                cvcField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (newValue.length() > 3) {
                cvcField.setText(newValue.substring(0, 3));
            }
        });

        // Validación para nombre
        cardHolderField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                cardHolderField.setText(newValue.replaceAll("[^a-zA-Z\\s]", ""));
            }
        });
    }

    private String formatCardNumber(String cardNumber) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(cardNumber.charAt(i));
        }
        return formatted.toString();
    }

    private String formatExpiryDate(String expiry) {
        if (expiry.length() >= 2) {
            return expiry.substring(0, 2) + "/" + expiry.substring(2);
        }
        return expiry;
    }

    private void updateTotalLabels() {
        String totalText = String.format("$%.2f", session.getTotal());
        totalLabel.setText(totalText);
        bottomTotalLabel.setText("Total: " + totalText);
    }

    @FXML
    private void handleBack(ActionEvent event) {
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

    @FXML
    private void handleConfirmOrder(ActionEvent event) {
        if (validateFields()) {
            processOrder();
        } else {
            showValidationAlert();
        }
    }

    private boolean validateFields() {
        String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
        if (cardNumber.length() != 16) return false;

        String expiry = expiryField.getText();
        if (!expiry.matches("\\d{2}/\\d{2}")) return false;

        String cvc = cvcField.getText();
        if (cvc.length() != 3) return false;

        String cardHolder = cardHolderField.getText().trim();
        if (cardHolder.isEmpty()) return false;

        return true;
    }

    private void processOrder() {
        System.out.println("Procesando pedido...");
        System.out.println(session.getResumenCompleto());

        confirmButton.setDisable(true);
        confirmButton.setText("Procesando...");

        // Simular procesamiento
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(this::showSuccessAlert);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showValidationAlert() {
        showAlert("Campos incompletos",
                "Por favor completa todos los campos de pago correctamente.",
                Alert.AlertType.WARNING);
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Compra Confirmada!");
        alert.setHeaderText("Tu reserva ha sido procesada exitosamente");
        alert.setContentText("Recibirás un email con los detalles de tu compra y el código QR para el cine.\n\n" +
                "Total pagado: $" + String.format("%.2f", session.getTotal()));

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

        confirmButton.setDisable(false);
        confirmButton.setText("Confirmar Compra");
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