package dk.easv.moviecollectionproject.GUI.Controller;

import dk.easv.moviecollectionproject.BLL.BLCategory;
import dk.easv.moviecollectionproject.BLL.BLMovie;
import dk.easv.moviecollectionproject.GUI.Model.MLCategory;
import dk.easv.moviecollectionproject.GUI.Model.MLMovie;
import dk.easv.moviecollectionproject.GUI.Model.MLMovieInCategory;
import dk.easv.moviecollectionproject.BE.Movie;
import dk.easv.moviecollectionproject.BE.Category;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class MCController {

    // Movie TableView
    @FXML
    private TableView<Movie> movieTableView;

    @FXML
    private TableColumn<Movie, String> nameColumn;

    @FXML
    private TableColumn<Movie, Float> ratingColumn;

    @FXML
    private TableColumn<Movie, String> categoryColumn;

    @FXML
    private TableColumn<Movie, String> lastViewColumn;

    // Category TableView
    @FXML
    private TableView<Category> categoryTableView;

    @FXML
    private TableColumn<Category, String> categoryNameColumn;

    // Movies in Category TableView
    @FXML
    private TableView<Movie> movieInCategoryTableView;

    @FXML
    private TableColumn<Movie, String> categoryMovieColumn;

    // Search
    @FXML
    private TextField searchField;

    // Model instances
    private final MLMovie movieModel = new MLMovie();
    private final MLCategory categoryModel = new MLCategory();
    private final MLMovieInCategory movieInCategoryModel = new MLMovieInCategory();
    private final BLCategory blCategory = new BLCategory();
    private final BLMovie blMovie = new BLMovie();
    private final MLMovie movies = new MLMovie();
    private final MovieController movieController = new MovieController();

    private CategoryController categoryController;

    @FXML
    public void initialize() {
        // Configure TableView columns
        movieModel.configureColumns(nameColumn, ratingColumn, categoryColumn, lastViewColumn);
        categoryModel.configureColumns(categoryNameColumn);
        movieInCategoryModel.configureColumns(categoryMovieColumn);

        // Load data into models
        movieModel.loadMovies();
        categoryModel.loadCategories();

        // Bind models to TableViews
        movieTableView.setItems(movieModel.getMovies());
        categoryTableView.setItems(categoryModel.getCategories());
        movieInCategoryTableView.setItems(movieInCategoryModel.getMovieInCategory());


        categoryTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Category selectedMovie = categoryTableView.getSelectionModel().getSelectedItem();
                if (selectedMovie != null) {
                    movieInCategoryTableView.getItems().clear();
                    ObservableList<Movie> movieInCategory = FXCollections.observableArrayList(blMovie.getMovieByCategoryId(categoryTableView.getSelectionModel().getSelectedItem().getId()));
                    movieInCategoryTableView.setItems(movieInCategory);
                }
            }
        });

        searchField.setOnKeyReleased(event -> onSearchFieldUpdated());

        checkLastViewAndRating();

    }



    // Search
    @FXML
    private void onSearchFieldUpdated() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            movieTableView.setItems(FXCollections.observableArrayList(movies.getMovies()));
            System.out.println("Retrieve all movies to Movies ListView");
        } else {
            movieTableView.setItems(FXCollections.observableArrayList(movies.filterMovies(query)));
            System.out.println("Searching for " + query);
        }
    }

    // Movie Management
    public void onPlayMovieClicked() {
        // Ensure a movie is selected before proceeding
        if (movieTableView.getSelectionModel().getSelectedItem() == null) {
            showAlert("No Movie Selected", "Please select a movie from the list to play.");
            return; // Exit the method early
        }

        try {
            // Load the media player FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/moviecollectionproject/GUI/View/moviePlayer.fxml"));
            Parent root = loader.load();

            // Get the controller for the media player window
            MCMediaPlayer mcMediaPlayer = loader.getController();

            // Set the current controller as a reference
            mcMediaPlayer.setController(this);

            // Get the file path from the selected movie
            String mediaPath = movieTableView.getSelectionModel().getSelectedItem().getFilePath();

            if (mediaPath == null) {
                showAlert("Resource Not Found", "The movie file could not be found in resources.");
                return;
            }else{
                mcMediaPlayer.loadMedia(mediaPath);
            }

            // Set up and display the stage
            Stage stage = new Stage();
            stage.setTitle("Media Player - Playing Movie");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            // Log any unexpected errors
            e.printStackTrace();
            showAlert("Error", "An error occurred while trying to open the media player.");
        }
    }


    public void onAddMovieClicked() {
        movieController.onAddMovieClicked();
    }

    //edit pop up window FXML methods
    public void onEditMovieClicked() {
        Movie selectedMovie = movieTableView.getSelectionModel().getSelectedItem();
        if(selectedMovie != null) {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/moviecollectionproject/GUI/View/editMovieWindow.fxml"));
                Parent root = loader.load();
                MovieController movieController = loader.getController();
                movieController.onEditMovieClicked(selectedMovie);
                Stage stage = new Stage();
                stage.setTitle("Edit Movie");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();
                stage.setOnCloseRequest(event -> {refreshTableView();});
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            System.out.println("No Movie Selected");
        }


    }

    public void onEditButtonClicked(Category name) {
        Category selectedItem = categoryTableView.getSelectionModel().getSelectedItem();
        blCategory.updateCategory(selectedItem.getId(), name);

    }

    // movie & Category menuItems
    public void onDeleteMovieClicked() {
        blMovie.removeMovie(movieTableView.getSelectionModel().getSelectedItem().getId());
        refreshTableView();
    }

    public void onAddCategoryClicked() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/moviecollectionproject/GUI/View/addCategoryWindow.fxml"));
            Parent root = loader.load();

            // Get the controller for the pop-up window
            CategoryController categoryController = loader.getController();

            // Set the MCController as a reference in the CategoryController
            categoryController.setController(this);

            Stage stage = new Stage();
            stage.setTitle("Add Category");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(event -> {refreshTableView();});
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void onEditCategoryClicked() {
        try {
            // Load the FXML file for the edit category window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/moviecollectionproject/GUI/View/editCategoryWindow.fxml"));
            Parent root = loader.load();

            // Get the controller for the pop-up window
            CategoryController categoryController = loader.getController();

            // Ensure the MCController reference is set properly in the CategoryController
            categoryController.setController(this);

            // Get the selected category from the categoryTableView
            Category selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();

            // If a category is selected, pass it to the CategoryController
            if (selectedCategory != null) {
                categoryController.onEditCategoryClicked(selectedCategory);  // Pass the selected category for editing
            } else {
                System.out.println("No category selected for editing.");
                return;
            }

            // Open the edit category window as a pop-up
            Stage stage = new Stage();
            stage.setTitle("Edit Category");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(event -> {refreshTableView();});

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDeleteCategoryClicked() {
        Category selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            blCategory.removeCategory(selectedCategory.getId());
            refreshTableView();
        }
    }



    // Application Management
    public void refreshTableView() {
        movieTableView.getItems().clear();
        categoryTableView.getItems().clear();
        movieInCategoryTableView.getItems().clear();

        ObservableList<Movie> moviesObservableList = FXCollections.observableArrayList(movies.getMovies());
        movieTableView.setItems(moviesObservableList);

        ObservableList<Category> categoryObservable = FXCollections.observableArrayList(blCategory.getAllCategories());
        categoryTableView.setItems(categoryObservable);

        if(movieInCategoryTableView.getSelectionModel().getSelectedItem() != null) {
            ObservableList<Movie> movieInCategory = FXCollections.observableArrayList(blMovie.getMovieByCategoryId(movieInCategoryTableView.getSelectionModel().getSelectedItem().getId()));
            movieInCategoryTableView.setItems(movieInCategory);
        }


    }

    public void checkLastViewAndRating() {
        AtomicBoolean showMsg = new AtomicBoolean(false);
        movieTableView.getItems().forEach(movie -> {
            String lastViewDateStr = movie.getLastView().toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate lastViewDate = LocalDate.parse(lastViewDateStr, formatter);
            LocalDate twoYearsAgo = LocalDate.now().minusYears(2);
            if (lastViewDate.isBefore(twoYearsAgo) && movie.getRating() < 6) {
                showMsg.set(true);
            }
        });
        if (showMsg.get()) {
            showAlert("Reminder", "It’s time to clean up your movie collection! Please review and delete any movies with a personal rating below 6 that have not been watched in over 2 years.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setWidth(400);
        alert.setHeight(200);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
