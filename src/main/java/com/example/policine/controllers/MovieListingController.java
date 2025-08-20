package com.example.policine.controllers;

import com.example.policine.model.dao.PeliculaDAO;
import com.example.policine.model.dao.FuncionDAO;
import com.example.policine.model.dao.SalaDAO;
import com.example.policine.model.entities.Pelicula;
import com.example.policine.model.entities.Funcion;
import com.example.policine.model.entities.Sala;
import com.example.policine.model.session.BookingSession;
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
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

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
    @FXML private ImageView logoImageView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private ComboBox<String> scheduleComboBox;
    @FXML private ComboBox<String> roomComboBox;
    @FXML private DatePicker datePicker;
    @FXML private Button myReservationsButton;

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

    // Timeline for logo animation
    private Timeline logoRotationAnimation;

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
        setupEnhancedUI();
        loadInitialData();
        setupShowtimesPanel();

        // Load movies
        loadMovies();
    }

    // UI Setup Methods
    private void setupEnhancedUI() {
        setupLogoAnimation();
        setupSearchFieldAnimations();
        setupButtonHoverEffects();
        setupPanelAnimations();
    }

    private void setupLogoAnimation() {
        logoImageView.setOnMouseEntered(e -> {
            if (logoRotationAnimation != null) logoRotationAnimation.stop();
            logoRotationAnimation = new Timeline(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(logoImageView.rotateProperty(), 15, Interpolator.EASE_OUT))
            );
            logoRotationAnimation.play();
        });

        logoImageView.setOnMouseExited(e -> {
            if (logoRotationAnimation != null) logoRotationAnimation.stop();
            logoRotationAnimation = new Timeline(
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(logoImageView.rotateProperty(), 0, Interpolator.EASE_OUT))
            );
            logoRotationAnimation.play();
        });
    }

    private void setupSearchFieldAnimations() {
        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            Timeline focusAnimation = new Timeline();
            if (newVal) {
                focusAnimation.getKeyFrames().add(
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(searchField.scaleXProperty(), 1.02, Interpolator.EASE_OUT),
                                new KeyValue(searchField.scaleYProperty(), 1.02, Interpolator.EASE_OUT))
                );
                searchField.setStyle(searchField.getStyle() + "; -fx-border-color: #0ea5e9;");
            } else {
                focusAnimation.getKeyFrames().add(
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(searchField.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                                new KeyValue(searchField.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
                );
                searchField.setStyle(searchField.getStyle().replace("; -fx-border-color: #0ea5e9;", ""));
            }
            focusAnimation.play();
        });
    }

    private void setupButtonHoverEffects() {
        setupCloseButtonEffects();
        setupBookSeatsButtonEffects();
    }

    private void setupCloseButtonEffects() {
        closePanelButton.setOnMouseEntered(e -> {
            Timeline hoverIn = new Timeline(
                    new KeyFrame(Duration.millis(150),
                            new KeyValue(closePanelButton.scaleXProperty(), 1.1, Interpolator.EASE_OUT),
                            new KeyValue(closePanelButton.scaleYProperty(), 1.1, Interpolator.EASE_OUT))
            );
            closePanelButton.setStyle(closePanelButton.getStyle() + "; -fx-background-color: rgba(255,100,100,0.8);");
            hoverIn.play();
        });

        closePanelButton.setOnMouseExited(e -> {
            Timeline hoverOut = new Timeline(
                    new KeyFrame(Duration.millis(150),
                            new KeyValue(closePanelButton.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(closePanelButton.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
            );
            closePanelButton.setStyle(closePanelButton.getStyle().replace("; -fx-background-color: rgba(255,100,100,0.8);", ""));
            hoverOut.play();
        });
    }

    private void setupBookSeatsButtonEffects() {
        bookSeatsButton.setOnMouseEntered(e -> {
            if (!bookSeatsButton.isDisabled()) {
                Timeline hoverIn = new Timeline(
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(bookSeatsButton.scaleXProperty(), 1.05, Interpolator.EASE_OUT),
                                new KeyValue(bookSeatsButton.scaleYProperty(), 1.05, Interpolator.EASE_OUT))
                );
                hoverIn.play();
            }
        });

        bookSeatsButton.setOnMouseExited(e -> {
            Timeline hoverOut = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(bookSeatsButton.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(bookSeatsButton.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
            );
            hoverOut.play();
        });
    }

    private void setupPanelAnimations() {
        showtimesPanel.setTranslateX(400);
    }

    private void setupFilterControls() {
        loadGenres();

        scheduleComboBox.getItems().addAll(
                "Todos",
                "Matutino (06:00-12:00)",
                "Vespertino (12:00-18:00)",
                "Nocturno (18:00-00:00)"
        );

        loadRooms();

        // Set default values
        genreComboBox.setValue("Todos");
        scheduleComboBox.setValue("Todos");
        roomComboBox.setValue("Todas");
        datePicker.setValue(LocalDate.now());

        // Add listeners for real-time filtering
        genreComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterMovies());
        scheduleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterMovies());
        roomComboBox.valueProperty().addListener((obs, oldVal, newVal) -> filterMovies());
    }

    private void loadGenres() {
        try {
            allPeliculas = peliculaDAO.listarTodos();
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
            allPeliculas = peliculaDAO.listarTodos();
            filteredPeliculas = allPeliculas;
            allSalas = salaDAO.listarTodos();
        } catch (Exception e) {
            System.err.println("Error loading initial data: " + e.getMessage());
            showAlert("Error", "No se pudieron cargar los datos desde la base de datos", Alert.AlertType.ERROR);
        }
    }

    private void loadMovies() {
        if (!moviesFlowPane.getChildren().isEmpty()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), moviesFlowPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.3);
            fadeOut.setOnFinished(e -> {
                moviesFlowPane.getChildren().clear();
                addMoviesWithAnimation();
            });
            fadeOut.play();
        } else {
            addMoviesWithAnimation();
        }
    }

    private void addMoviesWithAnimation() {
        try {
            if (filteredPeliculas.isEmpty()) {
                displayNoMoviesMessage();
                return;
            }

            Timeline staggeredAnimation = new Timeline();
            for (int i = 0; i < filteredPeliculas.size(); i++) {
                final Pelicula pelicula = filteredPeliculas.get(i);
                KeyFrame keyFrame = new KeyFrame(
                        Duration.millis(i * 50),
                        e -> {
                            VBox movieCard = createMovieCard(pelicula);
                            moviesFlowPane.getChildren().add(movieCard);
                            animateMovieCard(movieCard);
                        }
                );
                staggeredAnimation.getKeyFrames().add(keyFrame);
            }

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), moviesFlowPane);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(e -> staggeredAnimation.play());
            fadeIn.play();
        } catch (Exception e) {
            System.err.println("Error loading movies: " + e.getMessage());
            showAlert("Error", "Error al cargar las películas", Alert.AlertType.ERROR);
        }
    }

    private void displayNoMoviesMessage() {
        VBox messageContainer = new VBox(20);
        messageContainer.setStyle("-fx-alignment: center; -fx-padding: 50;");

        Label icon = new Label("🎬");
        icon.setStyle("-fx-font-size: 48px;");

        Label titleLabel = new Label("No se encontraron películas");
        titleLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        titleLabel.setFont(Font.font("System Bold", 24));
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 3, 0, 0, 1);");

        String searchTerm = searchField.getText().trim();
        String genre = genreComboBox.getValue();

        StringBuilder messageBuilder = new StringBuilder("No hay películas disponibles");
        if (!searchTerm.isEmpty() || (genre != null && !genre.equals("Todos"))) {
            messageBuilder.append(" que coincidan con los filtros:");
            if (!searchTerm.isEmpty()) {
                messageBuilder.append("\n• Búsqueda: \"").append(searchTerm).append("\"");
            }
            if (genre != null && !genre.equals("Todos")) {
                messageBuilder.append("\n• Género: ").append(genre);
            }
        }

        Label messageLabel = new Label(messageBuilder.toString());
        messageLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
        messageLabel.setFont(Font.font(16));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setStyle("-fx-text-alignment: center; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 2, 0, 0, 1);");

        Button clearFiltersButton = new Button("Limpiar Filtros");
        clearFiltersButton.setStyle(
                "-fx-background-color: #3182ce; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-radius: 8; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 10 20; " +
                        "-fx-effect: dropshadow(gaussian, rgba(49,130,206,0.4), 4, 0, 0, 2);"
        );
        clearFiltersButton.setOnAction(e -> clearAllFilters());

        messageContainer.getChildren().addAll(icon, titleLabel, messageLabel, clearFiltersButton);
        moviesFlowPane.getChildren().add(messageContainer);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), moviesFlowPane);
        fadeIn.setFromValue(0.3);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void clearAllFilters() {
        searchField.clear();
        genreComboBox.setValue("Todos");
        scheduleComboBox.setValue("Todos");
        roomComboBox.setValue("Todas");
        datePicker.setValue(LocalDate.now());
        filterMovies();
    }

    private void animateMovieCard(VBox movieCard) {
        movieCard.setOpacity(0);
        movieCard.setScaleX(0.8);
        movieCard.setScaleY(0.8);

        Timeline cardAnimation = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(movieCard.opacityProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(movieCard.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(movieCard.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
        );
        cardAnimation.play();
    }

    private VBox createMovieCard(Pelicula pelicula) {
        VBox movieCard = new VBox();
        movieCard.setPrefSize(230, 390);
        movieCard.setMinSize(210, 370);
        movieCard.setMaxSize(260, 410);
        movieCard.setSpacing(12);
        movieCard.setStyle("-fx-background-color: #2d3748; -fx-background-radius: 12; -fx-padding: 18; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);");

        setupMovieCardHoverEffects(movieCard);

        // Movie poster container
        StackPane posterContainer = new StackPane();
        posterContainer.setPrefSize(194, 290);
        posterContainer.setStyle("-fx-background-color: #4a5568; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 2);");

        // Try to load image
        ImageView poster = new ImageView();
        try {
            String imagePath = "/images/" + pelicula.getTitulo().toLowerCase().replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            poster.setImage(image);
            poster.setFitWidth(194);
            poster.setFitHeight(290);
            poster.setPreserveRatio(true);
            poster.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 1);");
            posterContainer.getChildren().add(poster);
        } catch (Exception e) {
            Label placeholder = new Label(pelicula.getTitulo().length() > 15 ?
                    pelicula.getTitulo().substring(0, 15) + "..." : pelicula.getTitulo());
            placeholder.setTextFill(javafx.scene.paint.Color.WHITE);
            placeholder.setFont(Font.font("System Bold", 14));
            placeholder.setStyle("-fx-alignment: center; -fx-text-alignment: center; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 2, 0, 0, 1);");
            placeholder.setWrapText(true);
            placeholder.setMaxWidth(180);
            posterContainer.getChildren().add(placeholder);
        }

        // Movie info section
        VBox movieInfo = new VBox(6);

        Label titleLabel = new Label(pelicula.getTitulo());
        titleLabel.setTextFill(javafx.scene.paint.Color.WHITE);
        titleLabel.setFont(Font.font("System Bold", 14));
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(210);
        titleLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 2, 0, 0, 1);");

        HBox detailsBox = new HBox(10);
        Label ratingLabel = new Label("📊 " + pelicula.getClasificacion());
        ratingLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
        ratingLabel.setFont(Font.font(11));

        Label durationLabel = new Label("⏱️ " + pelicula.getDuracion() + " min");
        durationLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
        durationLabel.setFont(Font.font(11));

        detailsBox.getChildren().addAll(ratingLabel, durationLabel);

        Label genreLabel = new Label("🎭 " + pelicula.getGenero());
        genreLabel.setTextFill(javafx.scene.paint.Color.ORANGE);
        genreLabel.setFont(Font.font("System Bold", 12));

        Button viewShowtimesButton = new Button("🎬 Ver Funciones");
        viewShowtimesButton.setMaxWidth(Double.MAX_VALUE);
        viewShowtimesButton.setPrefHeight(38);
        viewShowtimesButton.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, #3182ce, #2c5aa0); -fx-text-fill: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(49,130,206,0.4), 4, 0, 0, 2);");

        // Button hover effects
        viewShowtimesButton.setOnMouseEntered(e -> {
            Timeline hoverIn = new Timeline(
                    new KeyFrame(Duration.millis(150),
                            new KeyValue(viewShowtimesButton.scaleXProperty(), 1.05, Interpolator.EASE_OUT),
                            new KeyValue(viewShowtimesButton.scaleYProperty(), 1.05, Interpolator.EASE_OUT))
            );
            hoverIn.play();
        });

        viewShowtimesButton.setOnMouseExited(e -> {
            Timeline hoverOut = new Timeline(
                    new KeyFrame(Duration.millis(150),
                            new KeyValue(viewShowtimesButton.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(viewShowtimesButton.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
            );
            hoverOut.play();
        });

        viewShowtimesButton.setOnAction(e -> showMovieShowtimes(pelicula));

        movieInfo.getChildren().addAll(titleLabel, detailsBox, genreLabel, viewShowtimesButton);
        movieCard.getChildren().addAll(posterContainer, movieInfo);

        return movieCard;
    }

    private void setupMovieCardHoverEffects(VBox movieCard) {
        movieCard.setOnMouseEntered(e -> {
            Timeline hoverIn = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(movieCard.scaleXProperty(), 1.03, Interpolator.EASE_OUT),
                            new KeyValue(movieCard.scaleYProperty(), 1.03, Interpolator.EASE_OUT),
                            new KeyValue(movieCard.translateYProperty(), -3, Interpolator.EASE_OUT))
            );

            String hoveredStyle = movieCard.getStyle().replace(
                    "dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3)",
                    "dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0, 0, 6)"
            );
            movieCard.setStyle(hoveredStyle);
            hoverIn.play();
        });

        movieCard.setOnMouseExited(e -> {
            Timeline hoverOut = new Timeline(
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(movieCard.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(movieCard.scaleYProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(movieCard.translateYProperty(), 0, Interpolator.EASE_OUT))
            );

            String originalStyle = movieCard.getStyle().replace(
                    "dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0, 0, 6)",
                    "dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3)"
            );
            movieCard.setStyle(originalStyle);
            hoverOut.play();
        });
    }

    private void setupShowtimesPanel() {
        showtimesPanel.setVisible(false);
        selectToday();
    }

    // Event handlers
    @FXML
    private void onSearchKeyReleased(KeyEvent event) {
        filterMovies();
    }


    @FXML
    private void onDateChanged() {
        selectedDate = datePicker.getValue();
        if (selectedMovie != null) {
            loadShowtimesForDate(selectedDate);
        }
    }

    @FXML
    private void openMyReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/history.fxml"));
            Parent historyRoot = loader.load();

            Stage currentStage = (Stage) myReservationsButton.getScene().getWindow();
            Scene historyScene = new Scene(historyRoot);
            currentStage.setScene(historyScene);
            currentStage.setTitle("Cinemax - Mis Reservas");
            currentStage.centerOnScreen();

            System.out.println("Navegando a historial de reservas");
        } catch (IOException e) {
            System.err.println("Error al abrir historial de reservas: " + e.getMessage());
            showAlert("Error", "No se pudo abrir el historial de reservas", Alert.AlertType.ERROR);
        }
    }

    private void filterMovies() {
        try {
            String searchText = searchField.getText().toLowerCase().trim();
            String selectedGenre = genreComboBox.getValue();
            String selectedSchedule = scheduleComboBox.getValue();
            String selectedRoom = roomComboBox.getValue();
            LocalDate filterDate = datePicker.getValue();

            filteredPeliculas = allPeliculas.stream()
                    .filter(pelicula -> {
                        // Filter by search text (title, genre, or any text content)
                        if (!searchText.isEmpty()) {
                            return pelicula.getTitulo().toLowerCase().contains(searchText) ||
                                    pelicula.getGenero().toLowerCase().contains(searchText) ||
                                    pelicula.getClasificacion().toLowerCase().contains(searchText);
                        }
                        return true;
                    })
                    .filter(pelicula -> {
                        // Filter by genre
                        return selectedGenre == null || selectedGenre.equals("Todos") ||
                                pelicula.getGenero().equals(selectedGenre);
                    })
                    .filter(pelicula -> {
                        // Filter by schedule and room availability
                        if ((selectedSchedule != null && !selectedSchedule.equals("Todos")) ||
                                (selectedRoom != null && !selectedRoom.equals("Todas")) ||
                                filterDate != null) {

                            return hasAvailableFunctions(pelicula, selectedSchedule, selectedRoom, filterDate);
                        }
                        return true;
                    })
                    .collect(Collectors.toList());

            loadMovies();
        } catch (Exception e) {
            System.err.println("Error filtering movies: " + e.getMessage());
        }
    }

    private boolean hasAvailableFunctions(Pelicula pelicula, String schedule, String room, LocalDate date) {
        try {
            LocalDate searchDate = (date != null) ? date : LocalDate.now();
            List<Funcion> funciones = funcionDAO.buscarPorPeliculaYFecha(pelicula.getIdPelicula(), searchDate);

            if (funciones.isEmpty()) return false;

            return funciones.stream().anyMatch(funcion -> {
                boolean matchesSchedule = matchesScheduleFilter(funcion.getHora(), schedule);
                boolean matchesRoom = true;

                if (room != null && !room.equals("Todas")) {
                    try {
                        Sala sala = salaDAO.buscarPorId(funcion.getIdSala());
                        matchesRoom = (sala != null && sala.getNombreSala().equals(room));
                    } catch (Exception e) {
                        matchesRoom = false;
                    }
                }

                return matchesSchedule && matchesRoom;
            });
        } catch (Exception e) {
            return false;
        }
    }

    // Showtimes panel methods
    private void showMovieShowtimes(Pelicula pelicula) {
        selectedMovie = pelicula;
        selectedMovieTitle.setText("🎬 Funciones de " + pelicula.getTitulo());

        showtimesPanel.setVisible(true);
        Timeline slideIn = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(showtimesPanel.translateXProperty(), 0, Interpolator.EASE_OUT))
        );
        slideIn.play();

        loadShowtimesForDate(selectedDate);
    }

    @FXML
    private void closeShowtimesPanel() {
        Timeline slideOut = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(showtimesPanel.translateXProperty(), 400, Interpolator.EASE_IN))
        );
        slideOut.setOnFinished(e -> {
            showtimesPanel.setVisible(false);
            selectedMovie = null;
            selectedFuncion = null;
        });
        slideOut.play();
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
        selectedDate = getNextDayOfWeek(DayOfWeek.SATURDAY);
        updateDateButtons("saturday");
        if (selectedMovie != null) {
            loadShowtimesForDate(selectedDate);
        }
    }

    @FXML
    private void selectSunday() {
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
        String defaultStyle = "-fx-background-color: #4a5568; -fx-text-fill: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);";
        String selectedStyle = "-fx-background-color: #3182ce; -fx-text-fill: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(49,130,206,0.4), 4, 0, 0, 2); -fx-font-weight: bold;";

        Button[] buttons = {todayButton, tomorrowButton, dayAfterButton, saturdayButton, sundayButton};
        String[] buttonIds = {"today", "tomorrow", "dayafter", "saturday", "sunday"};

        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            String buttonId = buttonIds[i];

            if (buttonId.equals(selectedDay)) {
                button.setStyle(selectedStyle);
                Timeline pulseAnimation = new Timeline(
                        new KeyFrame(Duration.millis(200),
                                new KeyValue(button.scaleXProperty(), 1.05, Interpolator.EASE_OUT),
                                new KeyValue(button.scaleYProperty(), 1.05, Interpolator.EASE_OUT)),
                        new KeyFrame(Duration.millis(400),
                                new KeyValue(button.scaleXProperty(), 1.0, Interpolator.EASE_IN),
                                new KeyValue(button.scaleYProperty(), 1.0, Interpolator.EASE_IN))
                );
                pulseAnimation.play();
            } else {
                button.setStyle(defaultStyle);
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            }

            setupDateButtonHoverEffects(button, buttonId.equals(selectedDay));
        }
    }

    private void setupDateButtonHoverEffects(Button button, boolean isSelected) {
        button.setOnMouseEntered(e -> {
            if (!isSelected) {
                Timeline hoverIn = new Timeline(
                        new KeyFrame(Duration.millis(150),
                                new KeyValue(button.scaleXProperty(), 1.02, Interpolator.EASE_OUT),
                                new KeyValue(button.scaleYProperty(), 1.02, Interpolator.EASE_OUT))
                );
                hoverIn.play();

                String hoveredStyle = button.getStyle().replace(
                        "-fx-background-color: #4a5568",
                        "-fx-background-color: #5a6578"
                );
                button.setStyle(hoveredStyle);
            }
        });

        button.setOnMouseExited(e -> {
            if (!isSelected) {
                Timeline hoverOut = new Timeline(
                        new KeyFrame(Duration.millis(150),
                                new KeyValue(button.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                                new KeyValue(button.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
                );
                hoverOut.play();

                String originalStyle = button.getStyle().replace(
                        "-fx-background-color: #5a6578",
                        "-fx-background-color: #4a5568"
                );
                button.setStyle(originalStyle);
            }
        });
    }

    private void loadShowtimesForDate(LocalDate date) {
        if (!showtimesContainer.getChildren().isEmpty()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), showtimesContainer);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                showtimesContainer.getChildren().clear();
                loadShowtimesData(date);
            });
            fadeOut.play();
        } else {
            loadShowtimesData(date);
        }
    }

    private void loadShowtimesData(LocalDate date) {
        if (selectedMovie == null) return;

        try {
            List<Funcion> funciones = funcionDAO.buscarPorPeliculaYFecha(selectedMovie.getIdPelicula(), date);

            if (funciones.isEmpty()) {
                displayNoShowtimesMessage("📅 No hay funciones disponibles para esta fecha");
                fadeInShowtimes();
                return;
            }

            // Apply filters
            String selectedSchedule = scheduleComboBox.getValue();
            if (selectedSchedule != null && !selectedSchedule.equals("Todos")) {
                funciones = funciones.stream()
                        .filter(funcion -> matchesScheduleFilter(funcion.getHora(), selectedSchedule))
                        .collect(Collectors.toList());
            }

            String selectedRoom = roomComboBox.getValue();
            if (selectedRoom != null && !selectedRoom.equals("Todas")) {
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

            if (funciones.isEmpty()) {
                displayNoShowtimesMessage("🔍 No hay funciones que coincidan con los filtros aplicados");
                fadeInShowtimes();
                return;
            }

            // Add showtime items with staggered animation
            Timeline staggeredShowtimes = new Timeline();
            for (int i = 0; i < funciones.size(); i++) {
                final Funcion funcion = funciones.get(i);
                KeyFrame keyFrame = new KeyFrame(
                        Duration.millis(i * 100),
                        e -> addShowtimeItem(funcion)
                );
                staggeredShowtimes.getKeyFrames().add(keyFrame);
            }
            staggeredShowtimes.setOnFinished(e -> fadeInShowtimes());
            staggeredShowtimes.play();

        } catch (Exception e) {
            System.err.println("Error loading showtimes: " + e.getMessage());
            displayNoShowtimesMessage("❌ Error al cargar las funciones");
            fadeInShowtimes();
        }
    }

    private void displayNoShowtimesMessage(String message) {
        Label noShowtimesLabel = new Label(message);
        noShowtimesLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
        noShowtimesLabel.setFont(Font.font(14));
        noShowtimesLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");
        noShowtimesLabel.setWrapText(true);
        noShowtimesLabel.setMaxWidth(300);
        showtimesContainer.getChildren().add(noShowtimesLabel);
    }

    private void fadeInShowtimes() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), showtimesContainer);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private boolean matchesScheduleFilter(LocalTime hora, String scheduleFilter) {
        if (scheduleFilter == null || scheduleFilter.equals("Todos")) {
            return true;
        }

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
            Sala sala = salaDAO.buscarPorId(funcion.getIdSala());
            if (sala == null) return;

            BorderPane showtimeItem = new BorderPane();
            showtimeItem.setPrefHeight(65);
            showtimeItem.setMinHeight(55);
            showtimeItem.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1a202c, #2d3748); -fx-background-radius: 8; -fx-padding: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 2);");

            setupShowtimeItemHoverEffects(showtimeItem);

            VBox leftInfo = new VBox(4);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            Label timeLabel = new Label("🕐 " + funcion.getHora().format(timeFormatter));
            timeLabel.setTextFill(javafx.scene.paint.Color.WHITE);
            timeLabel.setFont(Font.font("System Bold", 16));
            timeLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 2, 0, 0, 1);");

            Label roomLabel = new Label("🏛️ " + sala.getNombreSala() + " • Capacidad: " + sala.getCapacidad());
            roomLabel.setTextFill(javafx.scene.paint.Color.LIGHTGRAY);
            roomLabel.setFont(Font.font(12));

            leftInfo.getChildren().addAll(timeLabel, roomLabel);

            Label availabilityLabel = new Label("✅ Asientos\ndisponibles");
            availabilityLabel.setTextFill(javafx.scene.paint.Color.LIGHTGREEN);
            availabilityLabel.setFont(Font.font("System Bold", 10));
            availabilityLabel.setWrapText(true);
            availabilityLabel.setMaxWidth(100);
            availabilityLabel.setStyle("-fx-text-alignment: center; -fx-alignment: center; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");

            showtimeItem.setLeft(leftInfo);
            showtimeItem.setRight(availabilityLabel);

            showtimeItem.setOnMouseClicked(e -> selectShowtime(funcion, sala, showtimeItem));

            // Initial animation
            showtimeItem.setOpacity(0);
            showtimeItem.setScaleX(0.9);
            showtimeItem.setScaleY(0.9);

            showtimesContainer.getChildren().add(showtimeItem);

            Timeline itemAnimation = new Timeline(
                    new KeyFrame(Duration.millis(250),
                            new KeyValue(showtimeItem.opacityProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(showtimeItem.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(showtimeItem.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
            );
            itemAnimation.play();

        } catch (Exception e) {
            System.err.println("Error adding showtime item: " + e.getMessage());
        }
    }

    private void setupShowtimeItemHoverEffects(BorderPane showtimeItem) {
        showtimeItem.setOnMouseEntered(e -> {
            Timeline hoverIn = new Timeline(
                    new KeyFrame(Duration.millis(150),
                            new KeyValue(showtimeItem.scaleXProperty(), 1.02, Interpolator.EASE_OUT),
                            new KeyValue(showtimeItem.scaleYProperty(), 1.02, Interpolator.EASE_OUT))
            );

            String hoveredStyle = showtimeItem.getStyle().replace(
                    "dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 2)",
                    "dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 3)"
            );
            showtimeItem.setStyle(hoveredStyle + "; -fx-cursor: hand;");
            hoverIn.play();
        });

        showtimeItem.setOnMouseExited(e -> {
            Timeline hoverOut = new Timeline(
                    new KeyFrame(Duration.millis(150),
                            new KeyValue(showtimeItem.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(showtimeItem.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
            );

            String originalStyle = showtimeItem.getStyle()
                    .replace("dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0, 0, 3)",
                            "dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 0, 2)")
                    .replace("; -fx-cursor: hand;", "");
            showtimeItem.setStyle(originalStyle);
            hoverOut.play();
        });
    }

    private BorderPane selectedShowtimeItem = null;

    private void selectShowtime(Funcion funcion, Sala sala, BorderPane showtimeItem) {
        selectedFuncion = funcion;

        // Reset previous selection
        if (selectedShowtimeItem != null) {
            selectedShowtimeItem.setStyle(selectedShowtimeItem.getStyle()
                    .replace("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #3182ce, #2c5aa0)",
                            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1a202c, #2d3748)"));
        }

        // Highlight selected showtime
        selectedShowtimeItem = showtimeItem;
        showtimeItem.setStyle(showtimeItem.getStyle()
                .replace("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #1a202c, #2d3748)",
                        "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #3182ce, #2c5aa0)"));

        // Selection animation
        Timeline selectionAnimation = new Timeline(
                new KeyFrame(Duration.millis(0),
                        new KeyValue(showtimeItem.scaleXProperty(), 1.0),
                        new KeyValue(showtimeItem.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.millis(150),
                        new KeyValue(showtimeItem.scaleXProperty(), 1.05, Interpolator.EASE_OUT),
                        new KeyValue(showtimeItem.scaleYProperty(), 1.05, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(showtimeItem.scaleXProperty(), 1.02, Interpolator.EASE_IN),
                        new KeyValue(showtimeItem.scaleYProperty(), 1.02, Interpolator.EASE_IN))
        );
        selectionAnimation.play();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        System.out.println("Selected showtime: " + funcion.getHora().format(timeFormatter) +
                " in " + sala.getNombreSala());

        // Enable book button with animation
        bookSeatsButton.setDisable(false);
        Timeline enableAnimation = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(bookSeatsButton.opacityProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(bookSeatsButton.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(bookSeatsButton.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
        );
        enableAnimation.play();
    }

    @FXML
    private void bookSeats() throws IOException {
        if (selectedFuncion == null) {
            showAlert("⚠️ Selección requerida", "Por favor selecciona una función", Alert.AlertType.WARNING);
            return;
        }

        try {
            Sala salaSeleccionada = salaDAO.buscarPorId(selectedFuncion.getIdSala());
            if (salaSeleccionada == null) {
                showAlert("❌ Error", "Error al cargar información de la sala", Alert.AlertType.ERROR);
                return;
            }

            // Button loading animation
            String originalText = bookSeatsButton.getText();
            bookSeatsButton.setText("⏳ Cargando...");
            bookSeatsButton.setDisable(true);

            Timeline loadingAnimation = new Timeline(
                    new KeyFrame(Duration.millis(100),
                            new KeyValue(bookSeatsButton.scaleXProperty(), 0.95, Interpolator.EASE_OUT),
                            new KeyValue(bookSeatsButton.scaleYProperty(), 0.95, Interpolator.EASE_OUT)),
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(bookSeatsButton.scaleXProperty(), 1.0, Interpolator.EASE_IN),
                            new KeyValue(bookSeatsButton.scaleYProperty(), 1.0, Interpolator.EASE_IN))
            );

            loadingAnimation.setOnFinished(e -> {
                try {
                    BookingSession session = BookingSession.getInstance();
                    session.reset();
                    session.setMovieData(selectedMovie, selectedFuncion, salaSeleccionada);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/policine/seatSelection.fxml"));
                    Parent seatSelectionRoot = loader.load();

                    SeatSelectionController controller = loader.getController();
                    controller.initializeWithSessionData();

                    Stage currentStage = (Stage) bookSeatsButton.getScene().getWindow();
                    Scene seatSelectionScene = new Scene(seatSelectionRoot);
                    currentStage.setScene(seatSelectionScene);
                    currentStage.setTitle("Cinemax - Selecciona tus asientos");
                    currentStage.centerOnScreen();

                    System.out.println("Navegación exitosa a Seat Selection con la función: " + selectedFuncion.getIdFuncion());

                } catch (Exception ex) {
                    System.err.println("Error al navegar a selección de asientos: " + ex.getMessage());
                    showAlert("❌ Error", "Error al cargar la selección de asientos", Alert.AlertType.ERROR);

                    bookSeatsButton.setText(originalText);
                    bookSeatsButton.setDisable(false);
                }
            });

            loadingAnimation.play();

        } catch (Exception e) {
            System.err.println("Error al navegar a selección de asientos: " + e.getMessage());
            showAlert("❌ Error", "Error al cargar la selección de asientos", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().setStyle(
                "-fx-background-color: #2d3748; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-family: System; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 5);"
        );

        alert.getDialogPane().lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: #3182ce; " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-radius: 5; " +
                        "-fx-font-weight: bold;"
        );

        alert.showAndWait();
    }
}


