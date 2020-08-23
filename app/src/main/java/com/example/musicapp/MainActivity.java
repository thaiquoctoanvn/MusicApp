package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.adapter.PlayingListAdapter;
import com.example.musicapp.object.Banner;
import com.example.musicapp.object.DownloadedSong;
import com.example.musicapp.object.Song;
import com.example.musicapp.object.SongLocal;
import com.example.musicapp.service.ClearFromRecentService;
import com.example.musicapp.service.ControlMediaService;
import com.example.musicapp.service.DownloadMediaService;
import com.example.musicapp.viewmodel.VMMusicToMiniPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, ActionForNotification{

    BottomNavigationView bottomNavigationView;
    private ImageButton ibtnMiniPlayPause, ibtnMiniNext, ibtnDownload, ibtnCollapse, ibtnPlayPause, ibtnNext, ibtnPrevious;
    private ImageView ivMiniSongIcon, ivMainSongImage;
    private TextView tvMiniSongName, tvMiniSongSinger, tvMainSongName, tvMainSongSinger, tvStartedTime, tvFinishedTime;
    private RelativeLayout relativeLayoutMediaPlayer;
    private RelativeLayout layoutMiniPlayer, layoutMainPlayer;
    private NestedScrollView nestedScrollView;
    private BottomSheetBehavior bottomSheetBehavior;
    private NavController navController;
    private NavHostFragment navHostFragment;
    private SeekBar sbSongLength;
    private RecyclerView rvPlayingList;

    private VMMusicToMiniPlayer vmMusicToMiniPlayer;

    private PlayingListAdapter playingListAdapter;
    private NotificationManager notificationManager;
    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver downloadBroadcastReceiver;
    private BroadcastReceiver mediaCompletedReceiver;
    private BroadcastReceiver setUpDownloadReceiver;
    private Intent downloadServiceIntent;

    private ArrayList<Song> playingList; //Danh sách các bài hát trong player
    private int i = 0; //Thứ tự trong danh sách phát
    private long downloadId;
    private DownloadedSong downloadedSong; //Bài hát chuẩn bị download
    private final int REQUEST_CODE = 1010;

    private Song currentSong; //Bài hát hiện đang phát

    private Realm realm;

    private ControlMediaService controlMediaService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ControlMediaService.ControlMediaBinder binder = (ControlMediaService.ControlMediaBinder) service;
            controlMediaService = binder.GetInstance();
            controlMediaService.SetMainActivityInstance(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(downloadBroadcastReceiver);
        unregisterReceiver(mediaCompletedReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, ControlMediaService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetUpNavigation();
        ConnectView();
        ReadyPlayMusic();
        OnListeningBroadcastReceiver();
        registerReceiver(downloadBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS"));
        registerReceiver(mediaCompletedReceiver, new IntentFilter("Media-completed"));
        registerReceiver(setUpDownloadReceiver, new IntentFilter("Set-up-download-done"));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateChannel();
            startService(new Intent(getBaseContext(), ClearFromRecentService.class));
        }
        AskForPermission();
    }

    public void SetUpNavigation() {
        //Toolbar toolbar = findViewById(R.id.toolBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        //NavigationUI.setupWithNavController(toolbar, navHostFragment.getNavController());
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
    }

    private void ConnectView() {

        Realm.init(MainActivity.this);
        realm = Realm.getDefaultInstance();

        ivMiniSongIcon = (ImageView) findViewById(R.id.iv_miniicon);
        tvMiniSongName = (TextView) findViewById(R.id.tv_minisongname);
        tvMiniSongSinger = (TextView) findViewById(R.id.tv_minisongsinger);
        ibtnMiniPlayPause = (ImageButton) findViewById(R.id.ibtn_miniplaypause);
        ibtnMiniNext = (ImageButton) findViewById(R.id.ibtn_mininext);
        tvStartedTime = (TextView) findViewById(R.id.tv_currenttime);
        tvFinishedTime = (TextView) findViewById(R.id.tv_totaltime);
        sbSongLength = (SeekBar) findViewById(R.id.sb_songlength);
        ibtnPlayPause = (ImageButton) findViewById(R.id.ibtn_play);
        ibtnDownload = findViewById(R.id.ibtn_download);
        ibtnCollapse = findViewById(R.id.ibtn_collapse);
        ibtnNext = findViewById(R.id.ibtn_next);
        ibtnPrevious = findViewById(R.id.ibtn_previous);
        layoutMiniPlayer = findViewById(R.id.layout_miniplayer);
        layoutMainPlayer = findViewById(R.id.layout_mainplayer);
        relativeLayoutMediaPlayer = findViewById(R.id.relative_layout_bottom_sheet);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        bottomSheetBehavior = BottomSheetBehavior.from(relativeLayoutMediaPlayer);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        ViewGroup.LayoutParams params = relativeLayoutMediaPlayer.getLayoutParams();
        params.height = height * 85 / 100;
        relativeLayoutMediaPlayer.setLayoutParams(params);

        ivMainSongImage = (ImageView) findViewById(R.id.iv_songimage);
        tvMainSongName = (TextView) findViewById(R.id.tv_mainsongname);
        tvMainSongSinger = (TextView) findViewById(R.id.tv_mainsongsinger);
        rvPlayingList = (RecyclerView) findViewById(R.id.rv_playinglist);

        relativeLayoutMediaPlayer.setOnClickListener(MainActivity.this);
        nestedScrollView.setOnClickListener(MainActivity.this);
        ibtnDownload.setOnClickListener(MainActivity.this);
        ibtnCollapse.setOnClickListener(MainActivity.this);
        ibtnMiniPlayPause.setOnClickListener(MainActivity.this);
        ibtnPlayPause.setOnClickListener(MainActivity.this);
        ibtnMiniNext.setOnClickListener(MainActivity.this);
        ibtnNext.setOnClickListener(MainActivity.this);
        ibtnPrevious.setOnClickListener(MainActivity.this);



        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    ViewGroup.LayoutParams params = layoutMainPlayer.getLayoutParams();
                    params.height = height;
                    layoutMainPlayer.setLayoutParams(params);
                } else if(newState == BottomSheetBehavior.STATE_COLLAPSED) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                layoutMiniPlayer.setAlpha(1-slideOffset);
                layoutMainPlayer.setAlpha(slideOffset);
            }
        });

    }

    private void ReadyPlayMusic() {

        playingList = new ArrayList<>();
        vmMusicToMiniPlayer = new ViewModelProvider(MainActivity.this).get(VMMusicToMiniPlayer.class);
        vmMusicToMiniPlayer.getMusicFromBanner().observe(MainActivity.this, new Observer<Banner>() {
            @Override
            public void onChanged(Banner banner) {
                UpdateMusicPlayer(banner.getSongImage(), banner.getSongName(), banner.getSongSinger());
                PlayNewMedia(banner.getSongLink());
            }
        });

        vmMusicToMiniPlayer.getSongFromListSong().observe(MainActivity.this, new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                UpdateMusicPlayer(song.getSongImage(), song.getSongName(), song.getSongSingerName());
                PlayNewMedia(song.getSongLink());
            }
        });

        vmMusicToMiniPlayer.getPlayingList().observe(MainActivity.this, new Observer<ArrayList<Song>>() {
            @Override
            public void onChanged(ArrayList<Song> songs) {
                UpdateMusicPlayer(songs.get(0).getSongImage(), songs.get(0).getSongName(), songs.get(0).getSongSingerName());
                PlayNewMedia(songs.get(0).getSongLink());
                playingList = songs;
                playingListAdapter = new PlayingListAdapter(playingList);
                rvPlayingList.setAdapter(playingListAdapter);
                rvPlayingList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                currentSong = playingList.get(i);

                CreateNotification.createNotification(MainActivity.this, playingList.get(0), R.drawable.ic_pause_black_24dp, 0, playingList.size());
            }
        });
    }

    private void UpdateMusicPlayer(String songImage, String songName, String songSingerName) {
        tvMiniSongName.setText(songName);
        tvMiniSongSinger.setText(songSingerName);
        Picasso.get()
                .load(songImage)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(ivMiniSongIcon);

        Picasso.get()
                .load(songImage)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(ivMainSongImage);
        tvMainSongName.setText(songName);
        tvMainSongSinger.setText(songSingerName);
    }

    private void PlayNewMedia(final String songLink) {
        if(i != 0) {
            CreateNotification.createNotification(MainActivity.this, playingList.get(i), R.drawable.ic_pause_black_24dp, i, playingList.size());
        }
        controlMediaService.SetUpMedia(songLink);
        tvFinishedTime.setText(MilliSecondToTime(controlMediaService.GetMedia().getDuration()));
        ibtnPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
        ibtnMiniPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
        sbSongLength.setMax(controlMediaService.GetMedia().getDuration() / 1000);
        sbSongLength.setOnSeekBarChangeListener(MainActivity.this);
    }

    private void PlayOrPauseMedia() {
        if(controlMediaService.IsMediaPlaying()) {
            controlMediaService.PauseMedia();
            ibtnPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            ibtnMiniPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        } else {
            ibtnPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
            ibtnMiniPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
            controlMediaService.PlayMedia();

        }
    }

    private void MediaOutOfList() {
        controlMediaService.ReleaseMedia();
        sbSongLength.setProgress(0);
        ibtnPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        ibtnMiniPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        tvStartedTime.setText("0:00");
        tvFinishedTime.setText("0:00");
        i = 0;
    }

    //Gọi từ controlMediaService
    public void DisplayTimeLine(long currentDuration) {
        sbSongLength.setProgress((int) currentDuration / 1000);//Đổi millisec về sec /1000
        tvStartedTime.setText(MilliSecondToTime(currentDuration));
    }

    //Gọi từ controlMediaService
    public void SetSeekBarBuffer(int percent) {
        sbSongLength.setSecondaryProgress(percent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(controlMediaService.IsMediaNotNull() && fromUser) {
            long milliseconds = progress * 1000;
            controlMediaService.GetMedia().seekTo((int) milliseconds);//nhân lại 1000 để trả về dạng millisecond cho mediaplayer
            tvStartedTime.setText(MilliSecondToTime(milliseconds));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private String MilliSecondToTime(long durationInMillis) {
        String timerString = "";
        String secsString;

        int seconds = (int) (durationInMillis / 1000) % 60;
        int minutes = (int) (durationInMillis / (1000 * 60)) % 60;
//        long hours = (durationInMillis / (1000 * 60 * 60)) % 24;

//        if(hours > 0) {
//            timerString = hours + ":";
//        }
//        Log.e("Secs", String.valueOf(seconds));
        if(seconds < 10) {
            secsString = "0" + seconds;
        } else  {
            secsString = "" + seconds;
        }
        timerString = timerString + minutes + ":" + secsString;
        return  timerString;
    }

    private void WriteSongToLocal(final DownloadedSong songLocal) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(songLocal);
            }
        });
    }

    private void DownloadSong(Song song) {
        downloadServiceIntent = new Intent(MainActivity.this, DownloadMediaService.class);

        downloadServiceIntent.putExtra("Song-name", song.getSongName());
        downloadServiceIntent.putExtra("Song-singer", song.getSongSingerName());
        downloadServiceIntent.putExtra("Song-link-online", song.getSongLink());
        downloadServiceIntent.putExtra("Song-image", song.getSongImage());
        startService(downloadServiceIntent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.relative_layout_bottom_sheet: {
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            }
            case R.id.ibtn_download:
                if(currentSong != null) {
                    DownloadSong(currentSong);
                }
                break;
            case R.id.ibtn_collapse:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.ibtn_miniplaypause:
                if(playingList.size() > 0) {
                    OnTrackPlay();
                }
                break;
            case R.id.ibtn_play:
                if(playingList.size() > 0) {
                    OnTrackPlay();
                }
                break;
            case R.id.ibtn_mininext:
                if(playingList.size() > 0) {
                    OnTrackNext();
                }
                break;
            case R.id.ibtn_next:
                if(playingList.size() > 0) {
                    OnTrackNext();
                }
                break;
            case R.id.ibtn_previous:
                if(playingList.size() > 0) {
                    OnTrackPrevious();
                }
                break;
        }
    }

    private void CreateChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CreateNotification.CHANNEL_ID,
                    "Music App",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private void OnListeningBroadcastReceiver() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("actionname");
                switch(action) {
                    case CreateNotification.ACTION_PREVIOUS:
                        OnTrackPrevious();
                        break;
                    case CreateNotification.ACTION_PLAY:
                        if(controlMediaService.IsMediaPlaying()) {
                            OnTrackPause();
                        } else {
                            OnTrackPlay();
                        }
                        break;
                    case CreateNotification.ACTION_NEXT:
                        OnTrackNext();
                        break;
                }
            }
        };
        downloadBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Ngắt luồng tải khi đã tải xong
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, - 1);
                if(downloadId == id) {
                    Log.e("###", "Download completed");
                    WriteSongToLocal(downloadedSong);
                    stopService(downloadServiceIntent);
                }
            }
        };
        mediaCompletedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(playingList != null) {
                    i++;
                    if(i < playingList.size()) {
                        UpdateMusicPlayer(playingList.get(i).getSongImage(), playingList.get(i).getSongName(), playingList.get(i).getSongSingerName());
                        PlayNewMedia(playingList.get(i).getSongLink());

                        currentSong = playingList.get(i);

                    } else {
                        MediaOutOfList();
                    }
                }
            }
        };
        setUpDownloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                downloadId = intent.getExtras().getLong("Id-download");
                Log.e("Id-download", String.valueOf(downloadId));
                downloadedSong = new DownloadedSong();
                downloadedSong.setId(String.valueOf(System.currentTimeMillis()));
                downloadedSong.setSongName(intent.getExtras().getString("Song-name"));
                downloadedSong.setSinger(intent.getExtras().getString("Song-singer"));
                downloadedSong.setSongLinkLocal(intent.getExtras().getString("Song-link-local"));
                downloadedSong.setSongImage(intent.getExtras().getString("Song-image"));
            }
        };
    }

    @Override
    public void OnTrackPrevious() {
        i--;
        if(i < 0) {
            i = 0;
        }
        UpdateMusicPlayer(playingList.get(i).getSongImage(), playingList.get(i).getSongName(), playingList.get(i).getSongSingerName());
        PlayNewMedia(playingList.get(i).getSongLink());
        CreateNotification.createNotification(MainActivity.this, playingList.get(i), R.drawable.ic_pause_black_24dp, i, playingList.size());
    }

    @Override
    public void OnTrackPlay() {
        PlayOrPauseMedia();
        CreateNotification.createNotification(MainActivity.this, playingList.get(i), R.drawable.ic_pause_black_24dp, i, playingList.size());
    }

    @Override
    public void OnTrackPause() {
        PlayOrPauseMedia();
        CreateNotification.createNotification(MainActivity.this, playingList.get(i), R.drawable.ic_play_arrow_black_24dp, i, playingList.size());

    }

    @Override
    public void OnTrackNext() {
        i++;
        if(i >= playingList.size()) {
            MediaOutOfList();
        } else {
            UpdateMusicPlayer(playingList.get(i).getSongImage(), playingList.get(i).getSongName(), playingList.get(i).getSongSingerName());
            PlayNewMedia(playingList.get(i).getSongLink());
            CreateNotification.createNotification(MainActivity.this, playingList.get(i), R.drawable.ic_pause_black_24dp, i, playingList.size());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    private void AskForPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }
}
