package com.example.musicapp.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;

public class DownloadMediaService extends Service {

    private int serviceId;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("###", "Downloading");
        serviceId = startId;
        final String songName = intent.getExtras().getString("Song-name");
        final String songSinger = intent.getExtras().getString("Song-singer");
        final String songLinkOnline = intent.getExtras().getString("Song-link-online");
        final String songImage = intent.getExtras().getString("Song-image");
        final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Thread threadDownload = new Thread(new Runnable() {
            @Override
            public void run() {

                Uri uri = Uri.parse(songLinkOnline);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("Tải xuống " + songName);
                request.setDescription("Hãy chờ một lúc...");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                String folderPath = Environment.getExternalStorageDirectory() + File.separator + "MusicApp";
                String fileName = songName + System.currentTimeMillis();
                File folder = new File(folderPath);
                if(!folder.exists()) {
                    folder.mkdir();
                }

                File file = new File(folderPath, fileName);
                request.setDestinationUri(Uri.fromFile(file));
                long id = downloadManager.enqueue(request);

                Intent downloadCompleteIntent = new Intent("Set-up-download-done");
                downloadCompleteIntent.putExtra("Song-name", songName);
                downloadCompleteIntent.putExtra("Song-singer", songSinger);
                downloadCompleteIntent.putExtra("Song-link-local", folderPath + File.separator + fileName);
                downloadCompleteIntent.putExtra("Song-image", songImage);
                downloadCompleteIntent.putExtra("Id-download", id);
                sendBroadcast(downloadCompleteIntent);

            }
        });
        threadDownload.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf(serviceId);
    }
}
