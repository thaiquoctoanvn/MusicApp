package com.example.musicapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicapp.object.Song;
import com.example.musicapp.service.NotificationActionService;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel";

    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    public static Notification notification; //notification android app
    public static Bitmap bitmap;

    public static void createNotification(Context context, final Song song, int button, int position, int playListSize) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

//            new LoadBitmapForNotification().execute(song.getSongImage());
        Thread bitmapThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = Picasso.get().load(song.getSongImage()).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        bitmapThread.start();

        //Hành động tua lùi
        PendingIntent pendingIntentPrevious;
        int drwPrevious;
        if(position == 0) {
            pendingIntentPrevious = null;
            drwPrevious = 0;
        } else {
            Intent intentPrevious = new Intent(context, NotificationActionService.class);
            intentPrevious.putExtra("control-media","control");
            intentPrevious.setAction(ACTION_PREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0, intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
            drwPrevious = R.drawable.ic_skip_previous_black_24dp;
            bitmapThread.interrupt();
        }

        //Hành động play/pause
        Intent intentPlay = new Intent(context, NotificationActionService.class);
        intentPlay.putExtra("control-media","control");
        intentPlay.setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        //Hành động tua tới
        PendingIntent pendingIntentNext;
        int drwNext;
        if(position == playListSize) {
            pendingIntentNext = null;
            drwNext = 0;
        } else {
            Intent intentNext = new Intent(context, NotificationActionService.class);
            intentNext.putExtra("control-media","control");
            intentNext.setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
            drwNext = R.drawable.ic_skip_next_black_24dp;
            bitmapThread.interrupt();
        }

        //create notification
        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_library_music_black_24dp)
                .setContentTitle(song.getSongName())
                .setContentText(song.getSongSingerName())
                .setLargeIcon(bitmap)
                .setShowWhen(false)
                .addAction(drwPrevious, "Previous", pendingIntentPrevious)
                .addAction(button, "Play", pendingIntentPlay)
                .addAction(drwNext, "Next", pendingIntentNext)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        notificationManagerCompat.notify(1, notification);
        Log.e("###", "created notification");
    }
}
