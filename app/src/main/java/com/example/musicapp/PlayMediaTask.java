package com.example.musicapp;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

public class PlayMediaTask extends AsyncTask<String, Void, Integer> {

    private MediaPlayer mediaPlayer;

    public PlayMediaTask(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

    }

    @Override
    protected Integer doInBackground(String... strings) {
        return mediaPlayer.getDuration();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }
}
