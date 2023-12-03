package com.conmuaxadan.rhythm.controller;

import com.conmuaxadan.rhythm.util.ConvertSecondToMinute;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class RhythmController implements Initializable {
    @FXML
    private ToggleButton btnPlay, btnPause, btnNext, btnPre, btnReset;
    @FXML
    private ProgressBar progress;
    @FXML
    private Slider sliderProgress, sliderVolume;
    @FXML
    private Label lbSongName, lbCurrentTime, lbEndTime, lbTitle;
    @FXML
    private Button btnAddFolder, btnAddFile, btnHome,btnPlayingQueue;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File dir;
    private File file;
    private File[] files;

    private ArrayList<File> songs;
    private int songNumber;
    private Timer timer;
    private TimerTask timerTask;
    private boolean running;

    private boolean isPlaying;

    double current, end;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songs = new ArrayList<File>();
        initSongs("src/main/resources/com/conmuaxadan/rhythm/music");
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        lbSongName.setText(songs.get(songNumber).getName());

        sliderVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(sliderVolume.getValue() * 0.01);
            }
        });
        sliderProgress.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (sliderProgress.isValueChanging()) {
                    mediaPlayer.seek(Duration.seconds(media.getDuration().toSeconds() * (newValue.doubleValue() / 100)));
                    progress.setProgress(newValue.doubleValue() / 100);
                }
            }
        });
    }

    public void initSongs(String path) {
        dir = new File(path);
        files = dir.listFiles();
        if (dir.isDirectory()) {
            if (files != null)
                for (File file :
                        files) {
                    songs.add(file);
                }
        }
        if (dir.isFile()){
            songs.add(dir);
        }
    }

    @FXML
    public void playMedia() {
        beginTimer();
        mediaPlayer.setVolume(sliderVolume.getValue() * 0.01);
        mediaPlayer.play();
        isPlaying = true;

        btnPlay.setDisable(true);
        btnPlay.setVisible(false);

        btnPause.setDisable(false);
        btnPause.setVisible(true);
    }
    @FXML
    public void pauseMedia() {
        cancelTimer();
        mediaPlayer.pause();
        isPlaying = false;

        btnPause.setDisable(true);
        btnPause.setVisible(false);

        btnPlay.setDisable(false);
        btnPlay.setVisible(true);


    }

    @FXML
    public void nextMedia() throws MalformedURLException {
        if (songNumber < songs.size() - 1) {
            songNumber++;
            mediaPlayer.stop();

            isPlaying = false;
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            lbSongName.setText(songs.get(songNumber).getName());
            playMedia();
        } else {
            songNumber = 0;
            mediaPlayer.stop();

            isPlaying = false;
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            lbSongName.setText(songs.get(songNumber).getName());
            playMedia();
        }
    }

    @FXML
    public void previousMedia() throws MalformedURLException {
        if (songNumber > 0) {
            songNumber--;
            mediaPlayer.stop();
            isPlaying = false;
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            lbSongName.setText(songs.get(songNumber).getName());
            playMedia();
        } else {
            songNumber = songs.size() - 1;
            mediaPlayer.stop();
            isPlaying = false;
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            lbSongName.setText(songs.get(songNumber).getName());
            playMedia();
        }
    }

    @FXML
    public void loopMedia() {

    }

    public void beginTimer() {
        timer = new Timer();

        timerTask = new TimerTask() {

            public void run() {
                Platform.runLater(() -> {
                    running = true;
                    double current = mediaPlayer.getCurrentTime().toSeconds();
                    double end = media.getDuration().toSeconds();
                    progress.setProgress(current / end);
                    sliderProgress.setValue((current / end) * 100);
                    lbCurrentTime.setText(ConvertSecondToMinute.secondsToTimeFormat(current));
                    lbEndTime.setText(ConvertSecondToMinute.secondsToTimeFormat(end - current));

                    if (current / end == 1) {

                        cancelTimer();
                        try {
                            nextMedia();
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });

            }

        };
        timer.scheduleAtFixedRate(timerTask, 0, 100);
    }

    public void cancelTimer() {
        isPlaying = false;
        timer.cancel();
    }

    @FXML
    public void mousePressedSliderProgress() {
        double newValue = sliderProgress.getValue();
        mediaPlayer.seek(Duration.seconds(media.getDuration().toSeconds() * (newValue / 100)));
        progress.setProgress(newValue / 100);
    }

    @FXML
    public void addFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose your folder");
        directoryChooser.setInitialDirectory(new File("C:\\"));
        Stage stage = (Stage) btnAddFolder.getScene().getWindow();
        dir = directoryChooser.showDialog(stage);
        initSongs(dir.getAbsolutePath());
        System.out.println(dir.getAbsolutePath());

    }
    public void addFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your file");
        fileChooser.setInitialDirectory(new File("C:\\"));
        Stage stage = (Stage) btnAddFile.getScene().getWindow();
        dir = fileChooser.showOpenDialog(stage);
        initSongs(dir.getAbsolutePath());
        System.out.println(dir.getAbsolutePath());

    }
    public void homeAction(){
        lbTitle.setText("Home");

        btnAddFile.setDisable(true);
        btnAddFile.setVisible(false);

        btnAddFolder.setDisable(false);
        btnAddFolder.setVisible(true);

    }
    public void playingQueueAction(){
        lbTitle.setText("Playing Queue");

        btnAddFolder.setDisable(true);
        btnAddFolder.setVisible(false);

        btnAddFile.setDisable(false);
        btnAddFile.setVisible(true);
    }


}
