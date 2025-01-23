package dk.easv.moviecollectionproject.GUI.Controller;

import dk.easv.moviecollectionproject.BE.Movie;
import dk.easv.moviecollectionproject.GUI.Model.MLMovie;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;

public class MovieController {

    private Movie selectedMovie;
    private MCController mcController;

    @FXML
    private File selectedFile; // To hold the chosen file
    @FXML
    private TableView<Movie> movieTableView;

    @FXML
    private Button editMovieBtn;
    @FXML
    private TextField movieNameField;
    @FXML
    private TextField movieCategoryField;
    @FXML
    private TextField movieRatingField;
    @FXML
    private TextField movieFilePathField;
    @FXML
    private Button chooseMovie;

    @FXML
    public TextField editMovieNameField;

    @FXML
    public TextField editMovieCategoryField;

    @FXML
    public TextField editMovieRatingField;

    @FXML
    public TextField editMovieFileField;

    @FXML
    public Button editChooseMovie;







    @FXML
    public void onPlayMovieClicked(){
        try {

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/moviecollectionproject/GUI/View/moviePlayer.fxml"));
            Scene scene = new Scene(loader.load());

            // Create a new stage (window)
            Stage newStage = new Stage();
            newStage.setTitle("Playing Movie");
            newStage.setScene(scene);
            newStage.setResizable(false); // Disable resizing
            newStage.show();  // Show the new window

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAddMovieClicked() {
        try {

            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/moviecollectionproject/GUI/View/addMovieWindow.fxml"));
            Scene scene = new Scene(loader.load());
            Stage newStage = new Stage();
            newStage.setTitle("Add Movie");
            newStage.setScene(scene);
            newStage.setResizable(false); // Disable resizing
            newStage.show();  // Show the new window

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onEditMovieClicked(Movie movie) {
        this.selectedMovie = movie;
        if(selectedMovie != null){
            editMovieNameField.setText(selectedMovie.getName());
            editMovieCategoryField.setText(String.valueOf(selectedMovie.getCategory()));
            editMovieRatingField.setText(String.valueOf(selectedMovie.getRating()));
            editMovieFileField.setText(String.valueOf(selectedMovie.getFilePath()));
        }
    }


    @FXML
    public void editMovieCatalog() {
        System.out.println("Choose Movie button clicked.");
        // Add a FileChooser here if needed.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Movie File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Movie Files", "*.mp4", "*.mpeg4")
        );
        Stage stage = (Stage) editChooseMovie.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);


        if (selectedFile != null) {
            editMovieFileField.setText("movies/" + selectedFile.getName()); // Set only the file name in the text field
        }
    }


    @FXML
    public void movieCatalog() {
        System.out.println("Choose Movie button clicked.");
        // Add a FileChooser here if needed.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a Movie File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Movie Files", "*.mp4", "*.mpeg4")
        );
        Stage stage = (Stage) chooseMovie.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);


        if (selectedFile != null) {
            movieFilePathField.setText("movies/" + selectedFile.getName()); // Set only the file name in the text field
        }
    }


    @FXML
    public void onAddButtonClicked() throws IOException {
        Movie movie = new Movie();
        MLMovie mlMovie = new MLMovie();
        if(movieNameField.getText() != null && movieCategoryField.getText() != null && movieRatingField.getText() != null && movieFilePathField.getText() != null){
            movie.setName(movieNameField.getText());
            movie.setCategory(Integer.parseInt(movieCategoryField.getText()));
            movie.setRating(Float.parseFloat(movieRatingField.getText()));
            movie.setFilePath(movieFilePathField.getText());
            movie.setLastView(Date.valueOf("2021-01-17"));
            mlMovie.addMovie(movie);

            // Copy the file to the resources folder
            String filePath = selectedFile.getName();
            Path targetPath = Path.of("src/main/resources/movies/", filePath);
            Files.createDirectories(targetPath.getParent());
            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File should be copied successfully!");

        }
    }

    public void onEditButtonClicked() throws IOException {
        MLMovie mlMovie = new MLMovie();
        Movie movie = selectedMovie;
        movie.setName(editMovieNameField.getText());
        movie.setCategory(Integer.parseInt(editMovieCategoryField.getText()));
        movie.setRating(Float.parseFloat(editMovieRatingField.getText()));
        movie.setFilePath(editMovieFileField.getText());
        mlMovie.updateMovie(selectedMovie.getId(), movie);

    }


}



