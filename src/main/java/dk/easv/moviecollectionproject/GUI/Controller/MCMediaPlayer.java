package dk.easv.moviecollectionproject.GUI.Controller;

import dk.easv.moviecollectionproject.GUI.Model.MLMoviePlayer;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Path;

public class MCMediaPlayer {

    @FXML
    private MediaView mediaView;
    @FXML
    private ToggleButton playPauseButton;
    @FXML
    private Button skipForwardButton;
    @FXML
    private Button skipBackwardButton;
    @FXML
    private Slider volumeSlider;

    @FXML
    private Slider durationSlider;

    private MLMoviePlayer mlMoviePlayer;
    private boolean isPlaying = false;
    private MCController mcController;

    // Initialize the media player
    @FXML
    public void initialize() {
        // Initially, no media file is loaded
        mlMoviePlayer = null;
        mediaView.setPreserveRatio(false); // Allow stretching to fill the MediaView completely

        // Set up button actions
        playPauseButton.setOnAction(e -> togglePlayPause());
        skipForwardButton.setOnAction(e -> skipForward());
        skipBackwardButton.setOnAction(e -> skipBackward());
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> setVolume(newVal.doubleValue()));
        volumeSlider.setValue(100); // by default the value is on maximum

        // Set up the slider drag event listener
        durationSlider.setOnMouseDragged(e -> onSliderDrag());
        durationSlider.setOnMouseReleased(e -> onSliderRelease()); // Optional: We can update once the drag ends
    }

    // Slider Update Method
    private void updateSlider() {
        if (mlMoviePlayer != null && mlMoviePlayer.getMediaPlayer() != null) {
            Duration currentDuration = mlMoviePlayer.getMediaPlayer().getCurrentTime();
            durationSlider.setValue(currentDuration.toMillis());  // Set slider value based on current time
        }
    }

    // Handle the slider drag event to seek to the new position
    private void onSliderDrag() {
        if (mlMoviePlayer != null && mlMoviePlayer.getMediaPlayer() != null) {
            double newTimeInMillis = durationSlider.getValue();  // Get the new value of the slider (in milliseconds)
            mlMoviePlayer.getMediaPlayer().seek(Duration.millis(newTimeInMillis));  // Seek to the new position
        }
    }

    // Handle the release event after dragging (optional)
    private void onSliderRelease() {
        // Optionally, you could do something once the drag ends (e.g., show a confirmation message)
    }

    // Toggle Play/Pause state
    @FXML
    private void togglePlayPause() {
        MediaPlayer mediaPlayer = mlMoviePlayer.getMediaPlayer();
        if (mlMoviePlayer == null || mlMoviePlayer.getMediaPlayer() == null) {
            showAlert("No Media Selected", "Please select a media file to play.");
            return;
        }
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseButton.setText("▶");  // Change text to play
        } else {
            mediaPlayer.play();
            playPauseButton.setText("⏸");  // Change text to pause
        }
        isPlaying = !isPlaying;
    }

    // Skip forward in the video
    @FXML
    private void skipForward() {
        if (mlMoviePlayer != null) {
            MediaPlayer mediaPlayer = mlMoviePlayer.getMediaPlayer();
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));  // Skip forward 10 seconds
        }
    }

    // Skip backward in the video
    @FXML
    private void skipBackward() {
        if (mlMoviePlayer != null) {
            MediaPlayer mediaPlayer = mlMoviePlayer.getMediaPlayer();
            mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));  // Skip backward 10 seconds
        }
    }

    // Set the volume from the slider
    @FXML
    private void setVolume(double volume) {
        if (mlMoviePlayer != null) {
            mlMoviePlayer.setVolume(volume / 100.0); // Volume slider in percentage
        }
    }

    // Display an alert message
    @FXML
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to load media from a file path
    @FXML
    public void loadMedia(String mediaPath) {

        if (mediaPath != null) {

            File file = new File("src/main/resources/" + mediaPath);
            System.out.println(file.getAbsolutePath());
            if(!file.exists()){
                System.out.println("File Not Found");
            }else {
                System.out.println("Media path: " + mediaPath);  // Debugging line
                mlMoviePlayer = new MLMoviePlayer(mediaPath);    // Initialize MLMoviePlayer with the media path

                // Set MediaPlayer to MediaView after it is ready
                mlMoviePlayer.getMediaPlayer().setOnReady(() -> {
                    // Ensure the MediaPlayer is fully initialized before adding to MediaView
                    mediaView.setMediaPlayer(mlMoviePlayer.getMediaPlayer());

                    // Set default volume
                    mlMoviePlayer.getMediaPlayer().setVolume(volumeSlider.getValue() / 100.0);

                    // Set the total duration for the slider after the media is ready
                    Duration totalDuration = mlMoviePlayer.getMediaPlayer().getMedia().getDuration();
                    durationSlider.setMax(totalDuration.toMillis());  // Update the slider's max value to match the video's duration
                    mlMoviePlayer.getMediaPlayer().seek(Duration.seconds(0)); // Ensure the video starts at the beginning

                    // Automatically play the video when loaded
                    playPauseButton.setText("⏸");
                    togglePlayPause();
                    isPlaying = true;
                });

                // Update the slider as the video plays
                mlMoviePlayer.getMediaPlayer().currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    updateSlider();  // Update slider as the video plays
                });
            }
        } else {
            System.out.println("Media path is null!");  // Debugging line
            showAlert("Error", "Media file not found in resources.");
        }
    }

    public void setController(MCController mcController) {
    this.mcController = mcController;
    }
}
