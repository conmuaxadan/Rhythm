package com.conmuaxadan.rhythm.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongManagement {
    public List<File> songs;
    List<File> playingQueue;

    public SongManagement() {
        this.songs = new ArrayList<>();
        this.playingQueue = new ArrayList<>();
    }

    public List<File> getSongs() {
        return songs;
    }

    public List<File> getPlayingQueue() {
        return playingQueue;
    }

    public void initSongs(String path) {
        File fileSrc = new File(path);
        if (fileSrc.isDirectory()) {
            playingQueue.clear();
            for (File file :
                    fileSrc.listFiles()) {
                if (!duplicateCheck(file.getName()) || songs == null){
                    songs.add(file);
                }
            }
        }
        if (fileSrc.isFile()){
            if (!duplicateCheck(fileSrc.getName()) || songs == null){
                songs.add(fileSrc);
            }
        }
        playingQueue =new ArrayList<>(songs);
    }

    public void initPlayingQueue(String path){

    }

    public boolean duplicateCheck(String name) {
        return songs.stream().anyMatch(file -> file.getName().equals(name));
    }

    public static void main(String[] args) {


    }
}
