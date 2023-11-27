package com.conmuaxadan.rhythm.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class RhythmController implements Initializable {
    @FXML
    private ToggleButton btnPlay, btnNext, btnPre, btnReset;
    @FXML
    private ProgressBar progress;
    @FXML
    private Slider sliderProgress, sliderVolume;
    @FXML
    private Label lbSongName;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File dir;
    private File[] files;

    private ArrayList<File> songs;
    private int songNumber;
    private Timer timer;
    private TimerTask timerTask;
    private boolean running;

    private boolean isPlaying;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
songs = new ArrayList<File>();
dir = new File("src/main/resources/com/conmuaxadan/rhythm/music");
files = dir.listFiles();
if (files!=null)
    for (File file:
         files) {
        songs.add(file);
    }
media = new Media(songs.get(songNumber).toURI().toString());
mediaPlayer = new MediaPlayer(media);

lbSongName.setText(songs.get(songNumber).getName());
    }

    @FXML
    public void playMedia() throws MalformedURLException {
        if (btnPlay.isSelected()) {
            mediaPlayer.play();
            ImageView i1 = new ImageView(new Image(new File("src/main/resources/com/conmuaxadan/rhythm/img/pause-button.png").getAbsolutePath()));
            i1.setFitHeight(35);
            i1.setFitWidth(35);
            btnPlay.setGraphic(i1);
            isPlaying = true;
        }else{
            mediaPlayer.pause();
            ImageView i1 = new ImageView(new Image(new File("src/main/resources/com/conmuaxadan/rhythm/img/play-button.png").getAbsolutePath()));
            i1.setFitHeight(35);
            i1.setFitWidth(35);
            btnPlay.setGraphic(i1);
            isPlaying = false;
        }
    }
    @FXML
    public void nextMedia(){

    }
    @FXML
    public void previousMedia(){

    }
    @FXML
    public void loopMedia(){

    }


}
