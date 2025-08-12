package com.example.policine.controllers;

import com.example.policine.model.dao.FuncionDAO;
import com.example.policine.model.dao.PeliculaDAO;
import com.example.policine.model.dao.SalaDAO;
import com.example.policine.model.entities.Funcion;
import com.example.policine.model.entities.Pelicula;
import com.example.policine.model.entities.Sala;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.*;

public class SeatSelectionController implements Initializable {

    @FXML private Label lblTituloPelicula;
    @FXML private Label lblDetallesFuncion;
    @FXML private GridPane seatsGrid;
    @FXML private VBox selectedSeatsContainer;
    @FXML private ScrollPane selectedSeatsPane;
    @FXML private TextField txtCodigoDescuento;
    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblTotal;
    @FXML private Button btnContinuar;
    @FXML private Button btnVolver;

    // DAOs
    private FuncionDAO funcionDAO;
    private PeliculaDAO peliculaDAO;
    private SalaDAO salaDAO;

    // Data
    private Funcion funcionActual;
    private Pelicula peliculaActual;
    private Sala salaActual;

    // Seat management
    private Set<String> selectedSeats;
    private Set<String> occupiedSeats;
    private Map<String, Button> seatButtons;
    private Map<String, Double> seatPrices;

    // Pricing
    private final double PRECIO_NORMAL = 10.00;
    private final double PRECIO_PREMIUM = 15.00;
    private double descuentoAplicado = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarDAOs();
        inicializarColecciones();
        configurarInterfaz();

        // Cargar datos de ejemplo (puedes modificar esto para recibir parámetros)
        cargarDatosEjemplo();
    }

    private void inicializarDAOs() {
        funcionDAO = new FuncionDAO();
        peliculaDAO = new PeliculaDAO();
        salaDAO = new SalaDAO();
    }

    private void inicializarColecciones() {
        selectedSeats = new HashSet<>();
        occupiedSeats = new HashSet<>();
        seatButtons = new HashMap<>();
        seatPrices = new HashMap<>();

        // Simular algunos asientos ocupados
        occupiedSeats.addAll(Arrays.asList("A1", "A2", "C5", "D10", "F15", "H20"));
    }

    private void configurarInterfaz() {
        btnContinuar.setDisable(true);
        selectedSeatsPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Configurar listener para el código de descuento
        txtCodigoDescuento.textProperty().addListener((obs, oldVal, newVal) -> {
            aplicarDescuento();
        });
    }

    private void cargarDatosEjemplo() {
        // En un caso real, estos datos vendrían como parámetros
        // Por ahora usamos datos de ejemplo
        try {
            // Simular carga de función, película y sala
            funcionActual = new Funcion(1,
                    java.time.LocalDate.now(),
                    java.time.LocalTime.of(19, 45),
                    1, 2);

            peliculaActual = new Pelicula(1, "El Viaje del Héroe", "Aventura", "PG-13", 120, "Español", "Una épica aventura...");
            salaActual = new Sala(2, "Sala 2", 100, "Estándar", 1);

            actualizarInterfaz();
            generarAsientos();

        } catch (Exception e) {
            mostrarError("Error al cargar los datos: " + e.getMessage());
        }
    }

    private void actualizarInterfaz() {
        if (peliculaActual != null) {
            lblTituloPelicula.setText(peliculaActual.getTitulo());
        }

        if (funcionActual != null && salaActual != null) {
            String detalles = String.format("%s • %s • %s",
                    funcionActual.getHora().toString(),
                    salaActual.getNombreSala(),
                    "Hoy" // Puedes formatear la fecha como prefieras
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
        button.setPrefSize(35, 35);
        button.setFont(Font.font("System", FontWeight.BOLD, 10));

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
        }

        // Configurar acción del botón
        button.setOnAction(e -> toggleSeatSelection(seatId, button, tipoAsiento));

        return button;
    }

    private void configurarAsientoDisponible(Button button, String tipo) {
        String color = tipo.equals("premium") ? "#f39c12" : "#4a90e2";
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 3; -fx-border-color: transparent;",
                color
        ));
        button.setDisable(false);
    }

    private void configurarAsientoOcupado(Button button) {
        button.setStyle("-fx-background-color: #666666; -fx-text-fill: #999; -fx-background-radius: 3; -fx-border-color: transparent;");
        button.setDisable(true);
    }

    private void configurarAsientoSeleccionado(Button button, String tipo) {
        button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 3; -fx-border-color: transparent;");
    }

    private void toggleSeatSelection(String seatId, Button button, String tipo) {
        if (selectedSeats.contains(seatId)) {
            // Deseleccionar
            selectedSeats.remove(seatId);
            configurarAsientoDisponible(button, tipo);
        } else {
            // Seleccionar
            selectedSeats.add(seatId);
            configurarAsientoSeleccionado(button, tipo);
        }

        actualizarResumenSeleccion();
        actualizarPrecios();
    }

    private void actualizarResumenSeleccion() {
        selectedSeatsContainer.getChildren().clear();

        List<String> asientosOrdenados = new ArrayList<>(selectedSeats);
        Collections.sort(asientosOrdenados);

        for (String seatId : asientosOrdenados) {
            HBox seatItem = crearItemAsientoSeleccionado(seatId);
            selectedSeatsContainer.getChildren().add(seatItem);
        }

        btnContinuar.setDisable(selectedSeats.isEmpty());
    }

    private HBox crearItemAsientoSeleccionado(String seatId) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 5; -fx-padding: 8;");

        Label lblAsiento = new Label("Asiento " + seatId);
        lblAsiento.setTextFill(Color.WHITE);
        lblAsiento.setFont(Font.font("System", FontWeight.NORMAL, 12));

        // Determinar si es premium
        boolean isPremium = seatPrices.get(seatId) == PRECIO_PREMIUM;
        if (isPremium) {
            lblAsiento.setText("Asiento " + seatId + " (Premium)");
        }

        Label lblPrecio = new Label(String.format("$%.2f", seatPrices.get(seatId)));
        lblPrecio.setTextFill(Color.WHITE);
        lblPrecio.setFont(Font.font("System", FontWeight.BOLD, 12));

        Button btnRemover = new Button("×");
        btnRemover.setStyle("-fx-background-color: transparent; -fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px;");
        btnRemover.setOnAction(e -> {
            Button seatButton = seatButtons.get(seatId);
            String tipo = seatPrices.get(seatId) == PRECIO_PREMIUM ? "premium" : "normal";
            toggleSeatSelection(seatId, seatButton, tipo);
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

        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblDescuento.setText(String.format("-$%.2f", descuentoAplicado));
        lblTotal.setText(String.format("$%.2f", total));
    }

    private void aplicarDescuento() {
        String codigo = txtCodigoDescuento.getText().trim().toLowerCase();
        descuentoAplicado = 0.0;

        // Códigos de descuento de ejemplo
        switch (codigo) {
            case "desc10":
                descuentoAplicado = calcularSubtotal() * 0.10;
                break;
            case "desc20":
                descuentoAplicado = calcularSubtotal() * 0.20;
                break;
            case "estudiante":
                descuentoAplicado = calcularSubtotal() * 0.15;
                break;
        }

        actualizarPrecios();
    }

    private double calcularSubtotal() {
        return selectedSeats.stream()
                .mapToDouble(seat -> seatPrices.get(seat))
                .sum();
    }

    @FXML
    private void volverAtras() {
        // Implementar navegación hacia atrás
        // Por ejemplo, cerrar la ventana actual o navegar a la vista anterior
        System.out.println("Volviendo a la vista anterior...");

        // Ejemplo de cómo cerrar la ventana actual
        // ((Stage) btnVolver.getScene().getWindow()).close();
    }

    @FXML
    private void continuarAlCarrito() {
        if (selectedSeats.isEmpty()) {
            mostrarAlerta("Debe seleccionar al menos un asiento");
            return;
        }

        // Aquí implementarías la lógica para continuar al carrito
        System.out.println("Continuando al carrito con asientos: " + selectedSeats);
        System.out.println("Total a pagar: $" + String.format("%.2f", calcularSubtotal() - descuentoAplicado));

        // Ejemplo de navegación al carrito
        // navegarACarrito();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Métodos públicos para configurar datos desde otras vistas
    public void setFuncion(Funcion funcion) {
        this.funcionActual = funcion;
        if (funcion != null) {
            // Cargar película y sala relacionadas
            cargarPeliculaYSala();
            actualizarInterfaz();
        }
    }

    private void cargarPeliculaYSala() {
        try {
            if (funcionActual != null) {
                peliculaActual = peliculaDAO.buscarPorId(funcionActual.getIdPelicula());
                salaActual = salaDAO.buscarPorId(funcionActual.getIdSala());
            }
        } catch (Exception e) {
            System.err.println("Error al cargar película y sala: " + e.getMessage());
        }
    }

    // Getters para otros controladores
    public Set<String> getSelectedSeats() {
        return new HashSet<>(selectedSeats);
    }

    public Map<String, Double> getSeatPrices() {
        return new HashMap<>(seatPrices);
    }

    public double getTotalAmount() {
        return calcularSubtotal() - descuentoAplicado;
    }
}