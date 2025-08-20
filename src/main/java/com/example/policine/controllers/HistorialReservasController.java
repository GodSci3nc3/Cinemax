package com.example.policine.controllers;

import com.example.policine.model.dao.ReservaDAO;
import com.example.policine.model.dao.ReservaDAO.ReservaCompleta;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Random;

public class HistorialReservasController implements Initializable {

    @FXML private Button backButton;
    @FXML private Button newReservationButton;
    @FXML private Button contactButton;
    @FXML private VBox mainContentArea;
    @FXML private ScrollPane scrollPane;
    @FXML private Label emptyStateLabel;
    @FXML private VBox emptyStateContainer;

    private ReservaDAO reservaDAO;
    private int usuarioId = 1; // Este debería venir del login

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reservaDAO = new ReservaDAO();
        configurarEventos();
        cargarHistorialReservas();
    }

    private void configurarEventos() {
        backButton.setOnAction(e -> volverAlMenu());
        newReservationButton.setOnAction(e -> irANuevaReservacion());
        contactButton.setOnAction(e -> contactarSoporte());
    }

    private void cargarHistorialReservas() {
        List<ReservaCompleta> reservas = reservaDAO.listarReservasCompletas(usuarioId);

        if (reservas.isEmpty()) {
            mostrarEstadoVacio();
        } else {
            mostrarReservas(reservas);
        }
    }

    private void mostrarEstadoVacio() {
        emptyStateContainer.setVisible(true);
        scrollPane.setVisible(false);
    }

    private void mostrarReservas(List<ReservaCompleta> reservas) {
        emptyStateContainer.setVisible(false);
        scrollPane.setVisible(true);

        mainContentArea.getChildren().clear();

        for (ReservaCompleta reserva : reservas) {
            VBox tarjetaReserva = crearTarjetaReserva(reserva);
            mainContentArea.getChildren().add(tarjetaReserva);
        }
    }

    private VBox crearTarjetaReserva(ReservaCompleta reserva) {
        // Contenedor principal de la tarjeta
        VBox tarjeta = new VBox();
        tarjeta.setSpacing(15);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setStyle(
                "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1e3a5f, #2d4a73);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        );
        tarjeta.setMaxWidth(800);
        tarjeta.setAlignment(Pos.CENTER);

        // Header con información básica
        HBox header = crearHeaderReserva(reserva);

        // Separador
        Separator separador = new Separator();
        separador.setStyle("-fx-background-color: #4a90e2; -fx-opacity: 0.3;");

        // Contenido principal
        HBox contenidoPrincipal = crearContenidoPrincipal(reserva);

        // Footer con botones de acción
        HBox footer = crearFooterReserva(reserva);

        tarjeta.getChildren().addAll(header, separador, contenidoPrincipal, footer);

        // Margen entre tarjetas
        VBox.setMargin(tarjeta, new Insets(0, 0, 20, 0));

        return tarjeta;
    }

    private HBox crearHeaderReserva(ReservaCompleta reserva) {
        HBox header = new HBox();
        header.setSpacing(15);
        header.setAlignment(Pos.CENTER_LEFT);

        // Estado de la reserva
        Label estadoLabel = new Label(reserva.getEstadoFormateado());
        estadoLabel.setStyle(getEstiloEstado(reserva.estado));
        estadoLabel.setPadding(new Insets(5, 12, 5, 12));

        // ID de reserva
        Label idLabel = new Label("Reserva #" + reserva.idReserva);
        idLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Fecha de reserva
        Label fechaReservaLabel = new Label("Reservado: " + reserva.getFechaReservaFormateada());
        fechaReservaLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(estadoLabel, idLabel, spacer, fechaReservaLabel);
        return header;
    }

    private HBox crearContenidoPrincipal(ReservaCompleta reserva) {
        HBox contenido = new HBox();
        contenido.setSpacing(20);
        contenido.setAlignment(Pos.CENTER_LEFT);

        // Imagen de la película (placeholder)
        ImageView imageView = new ImageView();
        imageView.setFitWidth(120);
        imageView.setFitHeight(160);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

        try {
            // Seleccionar imagen aleatoria
            int numeroImagen = new Random().nextInt(4) + 1;
            String extension = (numeroImagen == 4) ? ".jpeg" : ".jpg";
            Image imagen = new Image(getClass().getResourceAsStream("/images/movie" + numeroImagen + extension));
            imageView.setImage(imagen);
        } catch (Exception e) {
            // Imagen por defecto si no se encuentra
            imageView.setStyle(imageView.getStyle() + "-fx-background-color: #4a5568;");
        }

        // Información de la película y función
        VBox infoPelicula = crearInfoPelicula(reserva);

        // Información de asientos y sala
        VBox infoAsientos = crearInfoAsientos(reserva);

        contenido.getChildren().addAll(imageView, infoPelicula, infoAsientos);
        return contenido;
    }

    private VBox crearInfoPelicula(ReservaCompleta reserva) {
        VBox info = new VBox();
        info.setSpacing(8);
        info.setAlignment(Pos.TOP_LEFT);

        // Título de la película
        Label titulo = new Label(reserva.tituloPelicula);
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        titulo.setWrapText(true);

        // Género y clasificación
        Label generoClasif = new Label(reserva.genero + " • " + reserva.clasificacion + " • " + reserva.getDuracionFormateada());
        generoClasif.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12px;");

        // Fecha y hora de función
        HBox fechaHora = new HBox(10);
        fechaHora.setAlignment(Pos.CENTER_LEFT);

        Label fechaIcon = new Label("📅");
        Label fechaTexto = new Label(reserva.getFechaFuncionFormateada());
        fechaTexto.setStyle("-fx-text-fill: #4a90e2; -fx-font-weight: bold;");

        Label horaIcon = new Label("🕒");
        Label horaTexto = new Label(reserva.getHoraFuncionFormateada());
        horaTexto.setStyle("-fx-text-fill: #4a90e2; -fx-font-weight: bold;");

        fechaHora.getChildren().addAll(fechaIcon, fechaTexto, horaIcon, horaTexto);

        info.getChildren().addAll(titulo, generoClasif, fechaHora);
        return info;
    }

    private VBox crearInfoAsientos(ReservaCompleta reserva) {
        VBox info = new VBox();
        info.setSpacing(8);
        info.setAlignment(Pos.TOP_RIGHT);
        info.setMinWidth(200);

        // Sala
        HBox salaInfo = new HBox(5);
        salaInfo.setAlignment(Pos.CENTER_RIGHT);
        Label salaIcon = new Label("🎭");
        Label salaTexto = new Label(reserva.nombreSala);
        salaTexto.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        salaInfo.getChildren().addAll(salaIcon, salaTexto);

        // Cantidad de asientos
        Label cantidadAsientos = new Label(reserva.cantidadAsientos + " asiento(s)");
        cantidadAsientos.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12px;");
        cantidadAsientos.setAlignment(Pos.CENTER_RIGHT);

        // Asientos específicos
        Label asientosLabel = new Label("Asientos:");
        asientosLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 11px;");
        asientosLabel.setAlignment(Pos.CENTER_RIGHT);

        Label asientosTexto = new Label(reserva.asientos != null ? reserva.asientos : "No especificados");
        asientosTexto.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold;");
        asientosTexto.setWrapText(true);
        asientosTexto.setAlignment(Pos.CENTER_RIGHT);
        asientosTexto.setMaxWidth(180);

        info.getChildren().addAll(salaInfo, cantidadAsientos, asientosLabel, asientosTexto);
        return info;
    }

    private HBox crearFooterReserva(ReservaCompleta reserva) {
        HBox footer = new HBox();
        footer.setSpacing(10);
        footer.setAlignment(Pos.CENTER_RIGHT);

        // Botón ver detalles
        Button verDetallesBtn = new Button("👁 Ver Detalles");
        verDetallesBtn.setStyle(
                "-fx-background-color: #4a90e2;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-radius: 6;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 5 15 5 15;"
        );
        verDetallesBtn.setOnAction(e -> verDetallesReserva(reserva));

        // Botón cancelar (solo si está confirmada o pendiente)
        if ("Confirmada".equalsIgnoreCase(reserva.estado) || "Pendiente".equalsIgnoreCase(reserva.estado)) {
            Button cancelarBtn = new Button("❌ Cancelar");
            cancelarBtn.setStyle(
                    "-fx-background-color: #e53e3e;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-radius: 6;" +
                            "-fx-font-size: 12px;" +
                            "-fx-padding: 5 15 5 15;"
            );
            cancelarBtn.setOnAction(e -> cancelarReserva(reserva));
            footer.getChildren().add(cancelarBtn);
        }

        footer.getChildren().add(verDetallesBtn);
        return footer;
    }

    private String getEstiloEstado(String estado) {
        switch (estado.toLowerCase()) {
            case "confirmada":
                return "-fx-background-color: #38a169; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;";
            case "pendiente":
                return "-fx-background-color: #d69e2e; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;";
            case "cancelada":
                return "-fx-background-color: #e53e3e; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;";
            default:
                return "-fx-background-color: #4a5568; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;";
        }
    }

    private void verDetallesReserva(ReservaCompleta reserva) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de la Reserva #" + reserva.idReserva);
        alert.setHeaderText(reserva.tituloPelicula);

        StringBuilder contenido = new StringBuilder();
        contenido.append("Estado: ").append(reserva.getEstadoFormateado()).append("\n\n");
        contenido.append("Película: ").append(reserva.tituloPelicula).append("\n");
        contenido.append("Género: ").append(reserva.genero).append("\n");
        contenido.append("Duración: ").append(reserva.getDuracionFormateada()).append("\n");
        contenido.append("Clasificación: ").append(reserva.clasificacion).append("\n\n");
        contenido.append("Función:\n");
        contenido.append("  Fecha: ").append(reserva.getFechaFuncionFormateada()).append("\n");
        contenido.append("  Hora: ").append(reserva.getHoraFuncionFormateada()).append("\n");
        contenido.append("  Sala: ").append(reserva.nombreSala).append("\n\n");
        contenido.append("Asientos reservados: ").append(reserva.cantidadAsientos).append("\n");
        contenido.append("Ubicaciones: ").append(reserva.asientos != null ? reserva.asientos : "No especificados").append("\n\n");
        contenido.append("Fecha de reserva: ").append(reserva.getFechaReservaFormateada());

        alert.setContentText(contenido.toString());
        alert.showAndWait();
    }

    private void cancelarReserva(ReservaCompleta reserva) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar Reserva");
        confirmacion.setHeaderText("¿Estás seguro que deseas cancelar esta reserva?");
        confirmacion.setContentText("Reserva #" + reserva.idReserva + " - " + reserva.tituloPelicula +
                "\nFecha: " + reserva.getFechaFuncionFormateada() +
                " - " + reserva.getHoraFuncionFormateada());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Aquí implementarías la lógica de cancelación
                mostrarMensaje("Cancelación", "La funcionalidad de cancelación estará disponible próximamente.", Alert.AlertType.INFORMATION);
            }
        });
    }

    private void volverAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/movieListing.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void irANuevaReservacion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/movieListing.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) newReservationButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void contactarSoporte() {
        mostrarMensaje("Contactar Soporte",
                "Para soporte técnico puedes contactarnos a:\n\n" +
                        "📧 Email: soporte@policine.com\n" +
                        "📞 Teléfono: (867) 123-4567\n" +
                        "🕒 Horario: Lun-Dom 9:00 AM - 10:00 PM",
                Alert.AlertType.INFORMATION);
    }

    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}