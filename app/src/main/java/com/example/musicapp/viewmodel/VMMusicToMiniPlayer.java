package com.example.musicapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.object.Banner;
import com.example.musicapp.object.Song;

import java.util.ArrayList;
import java.util.List;

public class VMMusicToMiniPlayer extends ViewModel {
    private MutableLiveData<Banner> qc;
    private MutableLiveData<Song> song;
    private MutableLiveData<ArrayList<Song>> playingList;
    public MutableLiveData<Banner> getMusicFromBanner() {
        if(qc == null) {
            qc = new MutableLiveData<>();
        }
        return qc;
    }

    public MutableLiveData<Song> getSongFromListSong() {
        if(song == null) {
            song = new MutableLiveData<>();
        }
        return song;
    }

    public MutableLiveData<ArrayList<Song>> getPlayingList() {
        if(playingList == null) {
            playingList = new MutableLiveData<>();
        }
        return playingList;
    }
}
