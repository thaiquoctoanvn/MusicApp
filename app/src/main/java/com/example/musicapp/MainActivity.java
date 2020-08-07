package com.example.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.musicapp.object.Song;
import com.example.musicapp.service.ClearFromRecentService;
import com.example.musicapp.viewmodel.VMMusicToMiniPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener, ActionForNotification{

    BottomNavigationView bottomNavigationView;
    private ImageButton ibtnMiniPlayPause, ibtnMiniNext, ibtnLove, ibtnCollapse, ibtnPlayPause, ibtnNext, ibtnPrevious;
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
    private Handler handler = new Handler();
    private MediaPlayer mediaPlayer;
    private PlayMediaTask playMediaTask;
    private NotificationManager notificationManager;
    private BroadcastReceiver broadcastReceiver;

    private ArrayList<Song> playingList;
    private int i = 0;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetUpNavigation();
        ConnectView();

        ReadyPlayMusic();
        OnListeningBroadcastReceiver();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS"));
            startService(new Intent(getBaseContext(), ClearFromRecentService.class));
        }


    }
    public void SetUpNavigation() {
        //Toolbar toolbar = findViewById(R.id.toolBar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        //NavigationUI.setupWithNavController(toolbar, navHostFragment.getNavController());
        NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
    }

    private void ConnectView() {
        ivMiniSongIcon = (ImageView) findViewById(R.id.iv_miniicon);
        tvMiniSongName = (TextView) findViewById(R.id.tv_minisongname);
        tvMiniSongSinger = (TextView) findViewById(R.id.tv_minisongsinger);
        ibtnMiniPlayPause = (ImageButton) findViewById(R.id.ibtn_miniplaypause);
        ibtnMiniNext = (ImageButton) findViewById(R.id.ibtn_mininext);
        tvStartedTime = (TextView) findViewById(R.id.tv_currenttime);
        tvFinishedTime = (TextView) findViewById(R.id.tv_totaltime);
        sbSongLength = (SeekBar) findViewById(R.id.sb_songlength);
        ibtnPlayPause = (ImageButton) findViewById(R.id.ibtn_play);
        ibtnLove = findViewById(R.id.ibtn_love);
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
        ibtnLove.setOnClickListener(MainActivity.this);
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
                    params.height = (height * 85 / 100) - 64;
                    layoutMainPlayer.setLayoutParams(params);
                } else if(newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                    layoutMiniPlayer.setVisibility(View.VISIBLE);
//                    layoutMiniPlayer.animate().alpha(1.0f).setDuration(200);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                ivMiniSongIcon.setAlpha(1-slideOffset);
//                tvMiniSongSinger.setAlpha(1-slideOffset);
//                tvMiniSongName.setAlpha(1-slideOffset);
//                ibtnMiniPlayPause.setAlpha(1-slideOffset);
//                ibtnMiniNext.setAlpha(1-slideOffset);
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
                PlayMedia(banner.getSongLink());
            }
        });

        vmMusicToMiniPlayer.getSongFromListSong().observe(MainActivity.this, new Observer<Song>() {
            @Override
            public void onChanged(Song song) {
                UpdateMusicPlayer(song.getSongImage(), song.getSongName(), song.getSongSingerName());
                PlayMedia(song.getSongLink());
            }
        });

        vmMusicToMiniPlayer.getPlayingList().observe(MainActivity.this, new Observer<ArrayList<Song>>() {
            @Override
            public void onChanged(ArrayList<Song> songs) {
                UpdateMusicPlayer(songs.get(0).getSongImage(), songs.get(0).getSongName(), songs.get(0).getSongSingerName());
                PlayMedia(songs.get(0).getSongLink());
                playingList = songs;
                playingListAdapter = new PlayingListAdapter(playingList);
                rvPlayingList.setAdapter(playingListAdapter);
                rvPlayingList.setLayoutManager(new LinearLayoutManager(MainActivity.this));

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

    private void PlayMedia(final String songLink) {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        } else if(mediaPlayer.isPlaying()) {
            mediaPlayer.reset();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(songLink);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvFinishedTime.setText(MilliSecondToTime(mediaPlayer.getDuration()));
        ibtnPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
        ibtnMiniPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
        sbSongLength.setMax(mediaPlayer.getDuration() / 1000);
        SetUpSeekBar();

        sbSongLength.setOnTouchListener(MainActivity.this);
        sbSongLength.setOnSeekBarChangeListener(MainActivity.this);
        mediaPlayer.setOnBufferingUpdateListener(MainActivity.this);
        mediaPlayer.setOnCompletionListener(MainActivity.this);
    }

    private void PauseMedia() {
        if(mediaPlayer.isPlaying()) {
            handler.removeCallbacks(UpdateCurrentTime);
            mediaPlayer.pause();
            ibtnPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            ibtnMiniPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        } else {
            mediaPlayer.start();
            ibtnPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
            ibtnMiniPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
            SetUpSeekBar();
        }
    }

    private Runnable UpdateCurrentTime = new Runnable() {
        @Override
        public void run() {

           long currentDuration = mediaPlayer.getCurrentPosition();
           sbSongLength.setProgress((int) currentDuration / 1000);//Đổi millisec về sec /1000
           tvStartedTime.setText(MilliSecondToTime(currentDuration));
           handler.postDelayed(UpdateCurrentTime, 1000);
        }
    };

    private void SetUpSeekBar() {

        if(mediaPlayer.isPlaying()) {
            handler.postDelayed(UpdateCurrentTime, 1000);
        }
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
        Log.e("Secs", String.valueOf(seconds));
        if(seconds < 10) {
            secsString = "0" + seconds;
        } else  {
            secsString = "" + seconds;
        }
        timerString = timerString + minutes + ":" + secsString;
        return  timerString;
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
            case R.id.ibtn_love:
                if(playingList.size() > 0) {
                    ibtnLove.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
                break;
            case R.id.ibtn_collapse:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.ibtn_miniplaypause:
                if(playingList.size() > 0) {
                    PauseMedia();
                }
                break;
            case R.id.ibtn_play:
                if(playingList.size() > 0) {
                    PauseMedia();
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        int playPosition = (int) (mediaPlayer.getDuration() / 100) * ((SeekBar) v).getProgress();
//        mediaPlayer.seekTo(playPosition);
//        tvStartedTime.setText(MilliSecondToTime(playPosition));
        return false;
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        sbSongLength.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        //Kiểm tra nếu list còn nhạc thì tăng thứ tự lên bài kế tiếp
        if(playingList != null) {
            i++;
            if(i < playingList.size()) {
                mp.reset();
                UpdateMusicPlayer(playingList.get(i).getSongImage(), playingList.get(i).getSongName(), playingList.get(i).getSongSingerName());
                PlayMedia(playingList.get(i).getSongLink());
            } else {
                mp.reset();
                sbSongLength.setProgress(0);
                ibtnPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                ibtnMiniPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                tvStartedTime.setText("0:00");
                tvFinishedTime.setText("0:00");
                i = 0;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(mediaPlayer != null && fromUser) {
            long milliseconds = progress * 1000;
            mediaPlayer.seekTo((int) milliseconds);//nhân lại 1000 để trả về dạng millisecond cho mediaplayer
            tvStartedTime.setText(MilliSecondToTime(milliseconds));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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
                        if(mediaPlayer.isPlaying()) {
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
    }

    @Override
    public void OnTrackPrevious() {
        i--;
        if(i < 0) {
            i = 0;
        }
        mediaPlayer.reset();
        UpdateMusicPlayer(playingList.get(i).getSongImage(), playingList.get(i).getSongName(), playingList.get(i).getSongSingerName());
        PlayMedia(playingList.get(i).getSongLink());
        CreateNotification.createNotification(MainActivity.this, playingList.get(i), R.drawable.ic_pause_black_24dp, i, playingList.size());
    }

    @Override
    public void OnTrackPlay() {
        PauseMedia();
        CreateNotification.createNotification(MainActivity.this, playingList.get(i), R.drawable.ic_pause_black_24dp, i, playingList.size());

    }

    @Override
    public void OnTrackPause() {
        PauseMedia();
        CreateNotification.createNotification(MainActivity.this, playingList.get(i), R.drawable.ic_play_arrow_black_24dp, i, playingList.size());

    }

    @Override
    public void OnTrackNext() {
        i++;
        if(i >= playingList.size()) {
            mediaPlayer.reset();
            sbSongLength.setProgress(0);
            ibtnPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            ibtnMiniPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            tvStartedTime.setText("0:00");
            tvFinishedTime.setText("0:00");
            i = 0;
        } else {
            mediaPlayer.reset();
            UpdateMusicPlayer(playingList.get(i).getSongImage(), playingList.get(i).getSongName(), playingList.get(i).getSongSingerName());
            PlayMedia(playingList.get(i).getSongLink());
            CreateNotification.createNotification(MainActivity.this, playingList.get(i), R.drawable.ic_pause_black_24dp, i, playingList.size());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }
}
