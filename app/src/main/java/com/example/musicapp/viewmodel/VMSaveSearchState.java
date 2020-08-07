package com.example.musicapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.object.Album;
import com.example.musicapp.object.Song;

import java.util.ArrayList;

public class VMSaveSearchState extends ViewModel {
    private MutableLiveData<ArrayList<Song>> listSearchSong;
    private MutableLiveData<ArrayList<Album>> listSearchAlbum;
    private MutableLiveData<String> keySearch;

    public MutableLiveData<ArrayList<Song>> getListSearchSong() {
        if(listSearchSong == null) {
            listSearchSong = new MutableLiveData<>();
        }
        return listSearchSong;
    }

    public MutableLiveData<ArrayList<Album>> getListSearchAlbum() {
        if(listSearchAlbum == null) {
            listSearchAlbum = new MutableLiveData<>();
        }
        return listSearchAlbum;
    }

    public MutableLiveData<String> getKeySearch() {
        if(keySearch == null) {
            keySearch = new MutableLiveData<>();
        }
        return keySearch;
    }
}
