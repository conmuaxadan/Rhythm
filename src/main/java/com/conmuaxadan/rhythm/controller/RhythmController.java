package com.conmuaxadan.rhythm.controller;

import com.conmuaxadan.rhythm.model.SongManagement;
import com.conmuaxadan.rhythm.util.ConvertSecondToMinute;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class RhythmController implements Initializable {
    @FXML
    private ToggleButton btnPlay, btnPause;
    @FXML
    private ProgressBar progress;
    @FXML
    private Slider sliderProgress, sliderVolume;
    @FXML
    private Label lbSongName, lbCurrentTime, lbEndTime, lbTitle, lbLoop;
    @FXML
    private Button btnAddFolder, btnAddFile;
    @FXML
    private ListView<File> listview;

    @FXML
    private MenuItem addFolder, addFile;

    private SongManagement songManagement;
    private File currentSong;
    private List<File> songs;

    private File fileSrc;

    private Media media;
    private MediaPlayer mediaPlayer;


    private int songNumber;
    private Timer timer;
    private TimerTask timerTask;
    private boolean running;

    private boolean isPlaying,isLoop, isHome, isPlayingQueue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isHome = true;
        songManagement = new SongManagement();
        songs = new ArrayList<>();

        initSongs("src/main/resources/com/conmuaxadan/rhythm/music");

        currentSong = songs.get(songNumber);
        media = new Media(currentSong.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        lbSongName.setText(currentSong.getName());

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
        listview.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    public void initSongs(String path) {
        if (isHome) {
            songManagement.initSongs(path);
            songs = new ArrayList<>(songManagement.getSongs());
        }else {
            songManagement.initPlayingQueue(path);
            songs = new ArrayList<>(songManagement.getPlayingQueue());
        }
        listview.getItems().clear();
        listview.getItems().addAll(songs);

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
        } else {
            songNumber = 0;
        }
      initPlay();

    }

    @FXML
    public void previousMedia() throws MalformedURLException {
        if (songNumber > 0) {
            songNumber--;
        } else {
            songNumber = songs.size() - 1;
        }
        initPlay();

    }

    public void initPlay(){
        mediaPlayer.stop();
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        lbSongName.setText(songs.get(songNumber).getName());
        playMedia();
        listview.getSelectionModel().select(songs.get(songNumber));
        listview.scrollTo(songs.get(songNumber));
    }

    @FXML
    public void loopMedia() {
        if (isLoop){
            isLoop = false;
            System.out.println("Loop off");
            lbLoop.setText("Loop: Off");
        }else {
            isLoop = true;
            System.out.println("Loop on");
            lbLoop.setText("Loop: On");
        }
    }

    @FXML
    public void shuffleList(){
        Collections.shuffle(songs);
        ListView<File> newListview = listview;
        newListview.getItems().clear();
        newListview.getItems().addAll(songs);

        newListview.getSelectionModel().select(currentSong);



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
                        if (isLoop){
                            mediaPlayer.seek(Duration.seconds(0));
                        }else {
                            cancelTimer();
                            try {
                                nextMedia();
                            } catch (MalformedURLException e) {
                                throw new RuntimeException(e);
                            }
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
    public void playInList(){
        songNumber = listview.getSelectionModel().getSelectedIndex();
        initPlay();
        playingQueueAction();
        initPlay();

        System.out.println(listview.getSelectionModel().getSelectedIndex());
    }

    @FXML
    public void addFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose your folder");
        directoryChooser.setInitialDirectory(new File("C:\\"));
        Stage stage = (Stage) lbSongName.getScene().getWindow();
        fileSrc = directoryChooser.showDialog(stage);
        initSongs(fileSrc.getAbsolutePath());
        System.out.println(fileSrc.getAbsolutePath());

    }

    public void addFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your file");
        fileChooser.setInitialDirectory(new File("C:\\"));
        Stage stage = (Stage) lbSongName.getScene().getWindow();
        fileSrc = fileChooser.showOpenDialog(stage);
        initSongs(fileSrc.getAbsolutePath());
        System.out.println(fileSrc.getAbsolutePath());

    }

    public void homeAction() {
        isHome = true;
        lbTitle.setText("Home");
        songs = new ArrayList<>(songManagement.getSongs());
        System.out.println("Home");
        ListView<File> newListview = listview;
        newListview.getItems().clear();
        newListview.getItems().addAll(songs);
        newListview.getSelectionModel().select(songs.get(songNumber));
        newListview.scrollTo(songs.get(songNumber));

    }

    public void playingQueueAction() {
        isHome = false;
        lbTitle.setText("Play Queue");
        songs = new ArrayList<>(songManagement.getPlayingQueue());
        ListView<File> newListview = listview;
        newListview.getItems().clear();
        newListview.getItems().addAll(songs);
        newListview.getSelectionModel().select(songs.get(songNumber));
        newListview.scrollTo(songs.get(songNumber));

    }


}
