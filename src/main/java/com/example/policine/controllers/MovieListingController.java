package com.example.policine.controllers;

import com.example.policine.model.dao.PeliculaDAO;
import com.example.policine.model.dao.FuncionDAO;
import com.example.policine.model.dao.SalaDAO;
import com.example.policine.model.entities.Pelicula;
import com.example.policine.model.entities.Funcion;
import com.example.policine.model.entities.Sala;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.util.ResourceBundle;
import java.util.List;
import java.util.stream.Collectors;

public class MovieListingController implements Initializable {

    // Header controls
    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private ComboBox<String> scheduleComboBox;
    @FXML private ComboBox<String> roomComboBox;
    @FXML private DatePicker datePicker;

    // Main content
    @FXML private ScrollPane moviesScrollPane;
    @FXML private FlowPane moviesFlowPane;

    // Showtimes panel
    @FXML private VBox showtimesPanel;
    @FXML private Label selectedMovieTitle;
    @FXML private Button closePanelButton;

    // Date selection buttons
    @FXML private Button todayButton;
    @FXML private Button tomorrowButton;
    @FXML private Button dayAfterButton;
    @FXML private Button saturdayButton;
    @FXML private Button sundayButton;

    @FXML private VBox showtimesContainer;
    @FXML private Button bookSeatsButton;

    // DAOs
    private PeliculaDAO peliculaDAO;
    private FuncionDAO funcionDAO;
    private SalaDAO salaDAO;

    // Data lists
    private List<Pelicula> allPeliculas;
    private List<Pelicula> filteredPeliculas;
    private List<Sala> allSalas;

    // Current selection
    private Pelicula selectedMovie;
    private LocalDate selectedDate;
    private Funcion selectedFuncion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize DAOs
        peliculaDAO = new PeliculaDAO();
        funcionDAO = new FuncionDAO();
        salaDAO = new SalaDAO();

        // Initialize date
        selectedDate = LocalDate.now();

        // Setup UI components
        setupFilterControls();
        loadInitialData();
        setupShowtimesPanel();

        // Load movies
        loadMovies();
    }

    private void setupFilterControls() {
        // Load genres from database
        loadGenres();

        // Setup schedule options
        scheduleComboBox.getItems().addAll("Todos", "Matutino (06:00-12:00)", "Vespertino (12:00-18:00)", "Nocturno (18:00-00:00)");

        // Load rooms from database
        loadRooms();

        // Set default values
        genreComboBox.setValue("Todos");
        scheduleComboBox.setValue("Todos");
        roomComboBox.setValue("Todas");
        datePicker.setValue(LocalDate.now());
    }

    private void loadGenres() {
        try {
            allPeliculas = peliculaDAO.listarTodos();

            // Extract unique genres
            List<String> genres = allPeliculas.stream()
                    .map(Pelicula::getGenero)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            genreComboBox.getItems().clear();
            genreComboBox.getItems().add("Todos");
            genreComboBox.getItems().addAll(genres);

        } catch (Exception e) {
            System.err.println("Error loading genres: " + e.getMessage());
            showAlert("Error", "No se pudieron cargar los géneros desde la base de datos", Alert.AlertType.ERROR);
        }
    }

    private void loadRooms() {
        try {
            allSalas = salaDAO.listarTodos();

            roomComboBox.getItems().clear();
            roomComboBox.getItems().add("Todas");

            for (Sala sala : allSalas) {
                roomComboBox.getItems().add(sala.getNombreSala());
            }

        } catch (Exception e) {
            System.err.println("Error loading rooms: " + e.getMessage());
            showAlert("Error", "No se pudieron cargar las salas desde la base de datos", Alert.AlertType.ERROR);
        }
    }

    private void loadInitialData() {
        try {
            // Load all movies
            allPeliculas = peliculaDAO.listarTodos();
            filteredPeliculas = allPeliculas;

            // Load all rooms
            allSalas = salaDAO.listarTodos();

        } catch (Exception e) {
            System.err.println("Error loading initial data: " + e.getMessage());
            showAlert("Error", "No se pudieron cargar los datos desde la base de datos", Alert.AlertType.ERROR);
        }
    }

    private void loadMovies() {
        // Clear existing movies
        moviesFlowPane.getChildren().clear();

        try {
            for (Pelicula pelicula : filteredPeliculas) {
                addMovieCard(pelicula);
            }

            if (filteredPeliculas.isEmpty()) {
                Label noMoviesLabel = new Label("No se encontraron películas con los filtros aplicados");
                noMoviesLabel.setTextFill(javafx.scene.paint.Color.WHITE);
                noMoviesLabel.setFont(Font.font(16));
                moviesFlowPane.getChildren().add(noMoviesLabel);
            }

        } catch (Exception e) {
            System.err.println("Error loading movies: " + e.getMessage());
            showAlert("Error", "Error al cargar las películas", Alert.AlertType.ERROR);
        }
    }

    private void addMovieCard(Pelicula pelicula) {
        VBox movieCard = new VBox();
        movieCard.setPrefSize(220, 380);
        movieCard.setMinSize(200, 360);
        movieCard.setMaxSize(250, 400);
        movieCard.setSpacing(10);
        movieCard.setStyle("-fx-background-color: #2d3748; -fx-background-radius: 10; -fx-padding: 15;");

        // Movie poster placeholder
        StackPane posterContainer = new StackPane();
        posterContainer.setPrefSize(190, 280);
        posterContainer.setMinSize(170, 250);
        posterContainer.setMaxSize(220, 310);
        posterContainer.setStyle("-fx-background-color: #4a5568; -fx-background-radius: 8;");

        // Try to load image, use placeholder if not found
        ImageView poster = new ImageView();
        try {
            // You can implement image loading logic here based on movie title or ID
            String imagePath = "/images/" + pelicula.getTitulo().toLowerCase().replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            poster.setImage(image);
            poster.setFitWidth(190);
            poster.setFitHeight(280);
            poster.setPreserveRatio(true);
            posterContainer.getChildren().add(poster);
        } catch (Exception e) {
            // Create a placeholder with movie title
            Label placeholder = new Label(pelicula.getTitulo().length() > 15 ?
                    pelicula.getTitulo().substring(0, 15) + "..." : pelicula.getTitulo());
            placeholder.setTextFill(javafx.scene.paint.Color.WHITE);
            placeholder.setFont(Font.font("System Bold", 14));
            placeholder.setStyle("-fx-alignment: center; -fx-text-alignment: center;");
            placeholder.setWrapText(true);
            placeholder.setMaxWidth(180);
            posterContainer.getChildren().add(placeholder);
        }

        // Movie info
        VBox movieInfo = new VBox();
        movieInfo.setSpacing(5);

        Label titleLabel = new Label(pelicula.getTitulo());
        titleLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        titleLabel.setFont(Font.font("System Bold", 14));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(200);

        HBox detailsBox = new HBox();
        detailsBox.setSpacing(8);

        Label ratingLabel = new Label(pelicula.getClasificacion());
        ratingLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
        ratingLabel.setFont(Font.font(11));

        Label durationLabel = new Label(pelicula.getDuracion() + " min");
        durationLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
        durationLabel.setFont(Font.font(11));

        detailsBox.getChildren().addAll(ratingLabel, durationLabel);

        Label genreLabel = new Label(pelicula.getGenero());
        genreLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
        genreLabel.setFont(Font.font(12));

        Button viewShowtimesButton = new Button("Ver Funciones");
        viewShowtimesButton.setMaxWidth(Double.MAX_VALUE);
        viewShowtimesButton.setPrefHeight(35);
        viewShowtimesButton.setStyle("-fx-background-color: #3182ce; -fx-text-fill: white; -fx-background-radius: 5;");
        viewShowtimesButton.setOnAction(e -> showMovieShowtimes(pelicula));

        movieInfo.getChildren().addAll(titleLabel, detailsBox, genreLabel, viewShowtimesButton);
        movieCard.getChildren().addAll(posterContainer, movieInfo);

        moviesFlowPane.getChildren().add(movieCard);
    }

    private void setupShowtimesPanel() {
        // Initially hide the showtimes panel
        showtimesPanel.setVisible(false);

        // Set today as default
        selectToday();
    }

    // Event handlers for search and filters
    @FXML
    private void onSearchKeyReleased(KeyEvent event) {
        filterMovies();
    }

    @FXML
    private void onGenreFilterChanged() {
        filterMovies();
    }

    @FXML
    private void onScheduleFilterChanged() {
        filterMovies();
    }

    @FXML
    private void onRoomFilterChanged() {
        filterMovies();
    }

    @FXML
    private void onDateChanged() {
        selectedDate = datePicker.getValue();
        if (selectedMovie != null) {
            loadShowtimesForDate(selectedDate);
        }
    }

    private void filterMovies() {
        try {
            String searchText = searchField.getText().toLowerCase().trim();
            String selectedGenre = genreComboBox.getValue();

            filteredPeliculas = allPeliculas.stream()
                    .filter(pelicula -> {
                        // Filter by search text
                        if (!searchText.isEmpty()) {
                            return pelicula.getTitulo().toLowerCase().contains(searchText);
                        }
                        return true;
                    })
                    .filter(pelicula -> {
                        // Filter by genre
                        if (selectedGenre != null && !selectedGenre.equals("Todos")) {
                            return pelicula.getGenero().equals(selectedGenre);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            loadMovies();

        } catch (Exception e) {
            System.err.println("Error filtering movies: " + e.getMessage());
        }
    }

    // Showtimes panel event handlers
    private void showMovieShowtimes(Pelicula pelicula) {
        selectedMovie = pelicula;
        selectedMovieTitle.setText("Funciones de " + pelicula.getTitulo());
        showtimesPanel.setVisible(true);
        loadShowtimesForDate(selectedDate);
    }

    @FXML
    private void closeShowtimesPanel() {
        showtimesPanel.setVisible(false);
        selectedMovie = null;
        selectedFuncion = null;
    }

    // Date selection handlers
    @FXML
    private void selectToday() {
        selectedDate = LocalDate.now();
        updateDateButtons("today");
        if (selectedMovie != null) {
            loadShowtimesForDate(selectedDate);
        }
    }

    @FXML
    private void selectTomorrow() {
        selectedDate = LocalDate.now().plusDays(1);
        updateDateButtons("tomorrow");
        if (selectedMovie != null) {
            loadShowtimesForDate(selectedDate);
        }
    }

    @FXML
    private void selectDayAfter() {
        selectedDate = LocalDate.now().plusDays(2);
        updateDateButtons("dayafter");
        if (selectedMovie != null) {
            loadShowtimesForDate(selectedDate);
        }
    }

    @FXML
    private void selectSaturday() {
        // Find next Saturday
        selectedDate = getNextDayOfWeek(DayOfWeek.SATURDAY);
        updateDateButtons("saturday");
        if (selectedMovie != null) {
            loadShowtimesForDate(selectedDate);
        }
    }

    @FXML
    private void selectSunday() {
        // Find next Sunday
        selectedDate = getNextDayOfWeek(DayOfWeek.SUNDAY);
        updateDateButtons("sunday");
        if (selectedMovie != null) {
            loadShowtimesForDate(selectedDate);
        }
    }

    private LocalDate getNextDayOfWeek(DayOfWeek targetDay) {
        LocalDate date = LocalDate.now();
        while (date.getDayOfWeek() != targetDay) {
            date = date.plusDays(1);
        }
        return date;
    }

    private void updateDateButtons(String selectedDay) {
        // Reset all buttons to default style
        String defaultStyle = "-fx-background-color: #4a5568; -fx-text-fill: white; -fx-background-radius: 5;";
        String selectedStyle = "-fx-background-color: #3182ce; -fx-text-fill: white; -fx-background-radius: 5;";

        todayButton.setStyle(defaultStyle);
        tomorrowButton.setStyle(defaultStyle);
        dayAfterButton.setStyle(defaultStyle);
        saturdayButton.setStyle(defaultStyle);
        sundayButton.setStyle(defaultStyle);

        // Highlight selected button
        switch (selectedDay) {
            case "today":
                todayButton.setStyle(selectedStyle);
                break;
            case "tomorrow":
                tomorrowButton.setStyle(selectedStyle);
                break;
            case "dayafter":
                dayAfterButton.setStyle(selectedStyle);
                break;
            case "saturday":
                saturdayButton.setStyle(selectedStyle);
                break;
            case "sunday":
                sundayButton.setStyle(selectedStyle);
                break;
        }
    }

    private void loadShowtimesForDate(LocalDate date) {
        showtimesContainer.getChildren().clear();

        if (selectedMovie == null) return;

        try {
            // Get functions for the selected movie and date
            List<Funcion> funciones = funcionDAO.buscarPorPeliculaYFecha(selectedMovie.getIdPelicula(), date);

            if (funciones.isEmpty()) {
                Label noShowtimesLabel = new Label("No hay funciones disponibles para esta fecha");
                noShowtimesLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
                noShowtimesLabel.setFont(Font.font(14));
                showtimesContainer.getChildren().add(noShowtimesLabel);
                return;
            }

            // Filter by schedule if selected
            String selectedSchedule = scheduleComboBox.getValue();
            if (!selectedSchedule.equals("Todos")) {
                funciones = funciones.stream()
                        .filter(funcion -> matchesScheduleFilter(funcion.getHora(), selectedSchedule))
                        .collect(Collectors.toList());
            }

            // Filter by room if selected
            String selectedRoom = roomComboBox.getValue();
            if (!selectedRoom.equals("Todas")) {
                Sala targetSala = allSalas.stream()
                        .filter(sala -> sala.getNombreSala().equals(selectedRoom))
                        .findFirst().orElse(null);

                if (targetSala != null) {
                    int salaId = targetSala.getIdSala();
                    funciones = funciones.stream()
                            .filter(funcion -> funcion.getIdSala() == salaId)
                            .collect(Collectors.toList());
                }
            }

            // Add showtime items
            for (Funcion funcion : funciones) {
                addShowtimeItem(funcion);
            }

            if (funciones.isEmpty()) {
                Label noFilteredShowtimesLabel = new Label("No hay funciones que coincidan con los filtros aplicados");
                noFilteredShowtimesLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
                noFilteredShowtimesLabel.setFont(Font.font(14));
                showtimesContainer.getChildren().add(noFilteredShowtimesLabel);
            }

        } catch (Exception e) {
            System.err.println("Error loading showtimes: " + e.getMessage());
            showAlert("Error", "Error al cargar las funciones", Alert.AlertType.ERROR);
        }
    }

    private boolean matchesScheduleFilter(LocalTime hora, String scheduleFilter) {
        switch (scheduleFilter) {
            case "Matutino (06:00-12:00)":
                return hora.isAfter(LocalTime.of(5, 59)) && hora.isBefore(LocalTime.of(12, 1));
            case "Vespertino (12:00-18:00)":
                return hora.isAfter(LocalTime.of(11, 59)) && hora.isBefore(LocalTime.of(18, 1));
            case "Nocturno (18:00-00:00)":
                return hora.isAfter(LocalTime.of(17, 59)) || hora.isBefore(LocalTime.of(0, 1));
            default:
                return true;
        }
    }

    private void addShowtimeItem(Funcion funcion) {
        try {
            // Get sala information
            Sala sala = salaDAO.buscarPorId(funcion.getIdSala());
            if (sala == null) return;

            BorderPane showtimeItem = new BorderPane();
            showtimeItem.setPrefHeight(60);
            showtimeItem.setMinHeight(50);
            showtimeItem.setStyle("-fx-background-color: #1a202c; -fx-background-radius: 5; -fx-padding: 10;");

            VBox leftInfo = new VBox();
            leftInfo.setSpacing(3);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            Label timeLabel = new Label(funcion.getHora().format(timeFormatter));
            timeLabel.setTextFill(javafx.scene.paint.Color.WHITE);
            timeLabel.setFont(Font.font("System Bold", 16));

            Label roomLabel = new Label(sala.getNombreSala() + " • Capacidad: " + sala.getCapacidad());
            roomLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
            roomLabel.setFont(Font.font(12));

            leftInfo.getChildren().addAll(timeLabel, roomLabel);

            // Calculate available seats (this would need a reservations system)
            Label availabilityLabel = new Label("Asientos disponibles");
            availabilityLabel.setTextFill(javafx.scene.paint.Color.LIGHTGREEN);
            availabilityLabel.setFont(Font.font(10));
            availabilityLabel.setWrapText(true);
            availabilityLabel.setMaxWidth(100);

            showtimeItem.setLeft(leftInfo);
            showtimeItem.setRight(availabilityLabel);

            // Make the item clickable
            showtimeItem.setOnMouseClicked(e -> selectShowtime(funcion, sala));

            showtimesContainer.getChildren().add(showtimeItem);

        } catch (Exception e) {
            System.err.println("Error adding showtime item: " + e.getMessage());
        }
    }

    private void selectShowtime(Funcion funcion, Sala sala) {
        selectedFuncion = funcion;

        // Highlight selected showtime (you can implement visual feedback here)
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        System.out.println("Selected showtime: " + funcion.getHora().format(timeFormatter) +
                " in " + sala.getNombreSala());

        // Enable book button
        bookSeatsButton.setDisable(false);
    }

    @FXML
    private void bookSeats() throws IOException {
        if (selectedFuncion == null) {
            showAlert("Error", "Por favor selecciona una función", Alert.AlertType.WARNING);
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/seatSelection.fxml"));
        Parent movieListingRoot = loader.load();

        Stage currentStage = (Stage) bookSeatsButton.getScene().getWindow();

        Scene seatSelectionScene = new Scene(movieListingRoot);

        currentStage.setScene(seatSelectionScene);
        currentStage.setTitle("Cinemax - Selecciona tus asientos");

        currentStage.centerOnScreen();

        System.out.println("Navegación exitosa a Seat Selection con la función: " + selectedFuncion.getIdFuncion());

    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}