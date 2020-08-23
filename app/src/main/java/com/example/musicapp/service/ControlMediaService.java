package com.example.musicapp.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.musicapp.MainActivity;
import com.example.musicapp.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ControlMediaService extends Service implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    public MediaPlayer mediaPlayer;
    private MainActivity activity;
    private Handler handler;
    private Binder controlMediaBinder = new ControlMediaBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return controlMediaBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        activity.SetSeekBarBuffer(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        sendBroadcast(new Intent("Media-completed"));
    }

    public class ControlMediaBinder extends Binder {
        public ControlMediaService GetInstance() {
            return ControlMediaService.this;
        }
    }

    public void SetMainActivityInstance(MainActivity activity) {
        this.activity = activity;
    }

    public boolean IsMediaPlaying() {
        if(!mediaPlayer.isPlaying()) {
            return false;
        }
        return true;
    }

    public void SetUpMedia(final String songLink) {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else if(mediaPlayer.isPlaying() || mediaPlayer != null) {
            mediaPlayer.reset();
        }
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(songLink);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SetUpTimeLine();
        mediaPlayer.setOnBufferingUpdateListener(ControlMediaService.this);
        mediaPlayer.setOnCompletionListener(ControlMediaService.this);
    }

    public void PlayMedia() {
        mediaPlayer.start();
        SetUpTimeLine();
    }

    public void PauseMedia() {
        mediaPlayer.pause();
        handler.removeCallbacks(UpdateCurrentTime);
    }

    public void ReleaseMedia() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public boolean IsMediaNotNull() {
        if(mediaPlayer == null) {
            return false;
        }
        return true;
    }

    public MediaPlayer GetMedia() {
        return mediaPlayer;
    }

    private Runnable UpdateCurrentTime = new Runnable() {
        @Override
        public void run() {

            long currentDuration = mediaPlayer.getCurrentPosition();
//            Log.e("Secs", String.valueOf(currentDuration/1000));
            activity.DisplayTimeLine(currentDuration);
            handler.postDelayed(UpdateCurrentTime, 1000);
        }
    };

    private void SetUpTimeLine() {

        handler = new Handler();
        if(mediaPlayer.isPlaying()) {
            handler.postDelayed(UpdateCurrentTime, 1000);
        }
    }
}
