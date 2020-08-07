package com.example.musicapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.object.Song;

import java.util.ArrayList;
import java.util.Map;

public class VMLoadListSong extends ViewModel {

    private MutableLiveData<ArrayList<Song>> listSong;
    private MutableLiveData<Map<String, String>> listSongInfo;

    public MutableLiveData<ArrayList<Song>> getListSong() {
        if(listSong == null) {
            listSong = new MutableLiveData<>();

        }
        return listSong;
    }

    public MutableLiveData<Map<String, String>> getListSongInfo() {
        if(listSongInfo == null) {
            listSongInfo = new MutableLiveData<>();
        }
        return listSongInfo;
    }
}
