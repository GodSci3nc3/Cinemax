package com.example.policine.controllers;

import com.example.policine.model.session.BookingSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.animation.ScaleTransition;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class FoodSelectionController implements Initializable {

    // FXML Components
    @FXML private Button btnVolver;
    @FXML private GridPane productsGrid;

    // Productos
    @FXML private Label precioPalomitas;
    @FXML private Label precioRefresco;
    @FXML private Label precioNachos;
    @FXML private Label precioHotdog;
    @FXML private Label precioDulces;
    @FXML private Label precioCombo;

    // Botones de agregar
    @FXML private Button btnPalomitas;
    @FXML private Button btnRefresco;
    @FXML private Button btnNachos;
    @FXML private Button btnHotdog;
    @FXML private Button btnDulces;
    @FXML private Button btnCombo;

    // Carrito
    @FXML private Label lblItemsCarrito;
    @FXML private Label lblTotalCarrito;
    @FXML private Button btnVerCarrito;

    // Botones inferiores
    @FXML private Button btnNoGracias;
    @FXML private Button btnContinuar;

    // Variables del carrito
    private Map<String, Integer> carrito = new HashMap<>();
    private Map<String, Double> precios = new HashMap<>();
    private int totalItems = 0;
    private double totalPrecio = 0.0;

    // Session data
    private BookingSession session;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        session = BookingSession.getInstance();
        initializePrices();
        initializeCartDisplay();
        setupHoverEffects();
    }

    public void initializeWithSessionData() {
        // Los datos ya están cargados en la sesión
        // Mostrar información de la reserva actual si es necesario
        updateMovieInfo();
    }

    private void updateMovieInfo() {
        // Aquí podrías mostrar información de la película y asientos seleccionados
        // Por ejemplo, agregar un label que muestre "Película: [título] - Asientos: [lista]"
    }

    private void initializePrices() {
        precios.put("Palomitas Grandes", 8.00);  // Actualizado para coincidir con el checkout
        precios.put("Coca-Cola", 3.00);          // Actualizado
        precios.put("Chocolates Variados", 4.50); // Actualizado
        precios.put("Nachos con Queso", 9.75);
        precios.put("Hot Dog Clásico", 7.25);
        precios.put("Combo Familiar", 25.00);

        // Actualizar labels de precios en la UI (si existen)
        if (precioPalomitas != null) precioPalomitas.setText("$" + precios.get("Palomitas Grandes"));
        if (precioRefresco != null) precioRefresco.setText("$" + precios.get("Coca-Cola"));
        if (precioDulces != null) precioDulces.setText("$" + precios.get("Chocolates Variados"));
    }

    private void initializeCartDisplay() {
        updateCartDisplay();
        btnVerCarrito.setVisible(false);
    }

    private void setupHoverEffects() {
        // Efecto hover para botones de agregar
        Button[] addButtons = {btnPalomitas, btnRefresco, btnNachos, btnHotdog, btnDulces, btnCombo};

        for (Button button : addButtons) {
            if (button != null) {
                button.setOnMouseEntered(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
                    scale.setToX(1.1);
                    scale.setToY(1.1);
                    scale.play();
                    button.setStyle(button.getStyle() + "; -fx-background-color: #29b6f6;");
                });

                button.setOnMouseExited(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
                    scale.setToX(1.0);
                    scale.setToY(1.0);
                    scale.play();
                    button.setStyle(button.getStyle().replace("; -fx-background-color: #29b6f6;", ""));
                });
            }
        }
    }

    // Métodos para agregar productos al carrito
    @FXML
    private void handleAddPalomitas() {
        addToCart("Palomitas Grandes", btnPalomitas);
    }

    @FXML
    private void handleAddRefresco() {
        addToCart("Coca-Cola", btnRefresco);
    }

    @FXML
    private void handleAddNachos() {
        addToCart("Nachos con Queso", btnNachos);
    }

    @FXML
    private void handleAddHotdog() {
        addToCart("Hot Dog Clásico", btnHotdog);
    }

    @FXML
    private void handleAddDulces() {
        addToCart("Chocolates Variados", btnDulces);
    }

    @FXML
    private void handleAddCombo() {
        addToCart("Combo Familiar", btnCombo);
    }

    private void addToCart(String producto, Button button) {
        // Agregar al carrito
        carrito.put(producto, carrito.getOrDefault(producto, 0) + 1);
        totalItems++;
        totalPrecio += precios.get(producto);

        // Actualizar display del carrito
        updateCartDisplay();

        // Mostrar botón "Ver Carrito" si hay items
        if (totalItems > 0) {
            btnVerCarrito.setVisible(true);
        }

        // Animación de confirmación
        playAddAnimation(button);

        // Mostrar notificación temporal
        showAddNotification(producto);
    }

    private void updateCartDisplay() {
        lblItemsCarrito.setText(totalItems + " item" + (totalItems != 1 ? "s" : ""));
        lblTotalCarrito.setText(String.format("$%.2f", totalPrecio));

        // Cambiar color del total según la cantidad
        if (totalItems > 0) {
            lblTotalCarrito.setStyle("-fx-text-fill: #4fc3f7; -fx-font-size: 18px; -fx-font-weight: bold;");
            lblItemsCarrito.setStyle("-fx-text-fill: #81c784; -fx-font-size: 14px; -fx-font-weight: bold;");
        } else {
            lblTotalCarrito.setStyle("-fx-text-fill: #4fc3f7; -fx-font-size: 16px; -fx-font-weight: bold;");
            lblItemsCarrito.setStyle("-fx-text-fill: #81c784; -fx-font-size: 14px;");
        }
    }

    private void playAddAnimation(Button button) {
        if (button == null) return;

        // Animación de pulso cuando se agrega un item
        ScaleTransition pulse = new ScaleTransition(Duration.millis(150), button);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.3);
        pulse.setToY(1.3);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);

        // Cambiar temporalmente el color
        String originalStyle = button.getStyle();
        button.setStyle(originalStyle + "; -fx-background-color: #66bb6a;");

        pulse.setOnFinished(e -> {
            button.setStyle(originalStyle);
        });

        pulse.play();
    }

    private void showAddNotification(String producto) {
        System.out.println("Agregado al carrito: " + producto);
    }

    @FXML
    private void handleVerCarrito() {
        if (totalItems == 0) {
            showAlert("Carrito vacío", "No hay productos en el carrito.", Alert.AlertType.INFORMATION);
            return;
        }

        StringBuilder carritoContent = new StringBuilder();
        carritoContent.append("🛒 CARRITO DE COMPRAS\n\n");

        for (Map.Entry<String, Integer> entry : carrito.entrySet()) {
            String producto = entry.getKey();
            int cantidad = entry.getValue();
            double precio = precios.get(producto);

            carritoContent.append(String.format("• %s x%d - $%.2f c/u = $%.2f\n",
                    producto, cantidad, precio, precio * cantidad));
        }

        carritoContent.append(String.format("\n💰 TOTAL: $%.2f", totalPrecio));
        carritoContent.append(String.format("\n📦 Items: %d", totalItems));

        showAlert("Tu Carrito", carritoContent.toString(), Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleVolverClick() throws IOException {
        if (totalItems > 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar");
            alert.setHeaderText("¿Deseas volver?");
            alert.setContentText("Tienes " + totalItems + " items en el carrito. ¿Quieres volver sin proceder?");

            alert.showAndWait().ifPresent(response -> {
                if (response.getButtonData().isDefaultButton()) {
                    try {
                        goBack();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } else {
            goBack();
        }
    }

    @FXML
    private void handleNoGracias() throws IOException {
        // Continuar sin comida
        session.setFoodData(new HashMap<>(), new HashMap<>());
        proceedToCheckout();
    }

    @FXML
    private void handleContinuar() throws IOException {
        if (totalItems > 0) {
            // Guardar datos de comida en la sesión
            session.setFoodData(carrito, precios);
            proceedToCheckout();
        } else {
            showAlert("Carrito vacío",
                    "No has seleccionado ningún producto. Usa 'No, gracias' si no deseas comida.",
                    Alert.AlertType.INFORMATION);
        }
    }

    private void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/seatSelection.fxml"));
        Parent seatSelectionRoot = loader.load();

        // Obtener el controlador y reinicializarlo con los datos de la sesión
        SeatSelectionController controller = loader.getController();
        controller.initializeWithSessionData();

        Stage currentStage = (Stage) btnVolver.getScene().getWindow();
        Scene seatSelectionScene = new Scene(seatSelectionRoot);
        currentStage.setScene(seatSelectionScene);
        currentStage.setTitle("Cinemax - Selecciona tus asientos");
        currentStage.centerOnScreen();
    }

    private void proceedToCheckout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/orderSummary.fxml"));
        Parent orderSummaryRoot = loader.load();

        // Obtener el controlador y pasarle los datos
        OrderSummaryController controller = loader.getController();
        controller.initializeWithSessionData();

        Stage currentStage = (Stage) btnContinuar.getScene().getWindow();
        Scene orderSummaryScene = new Scene(orderSummaryRoot);
        currentStage.setScene(orderSummaryScene);
        currentStage.setTitle("Cinemax - Resumen del pedido");
        currentStage.centerOnScreen();

        System.out.println("Navegación exitosa a Order Summary");
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Métodos getter para obtener información del carrito
    public Map<String, Integer> getCarrito() {
        return new HashMap<>(carrito);
    }

    public double getTotalPrecio() {
        return totalPrecio;
    }

    public int getTotalItems() {
        return totalItems;
    }
}