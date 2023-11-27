package com.conmuaxadan.rhythm.util;

public class ConvertSecondToMinute {
    public static String secondsToTimeFormat(double totalSeconds) {
        int minutes = (int)totalSeconds / 60;
        int seconds = (int)totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
