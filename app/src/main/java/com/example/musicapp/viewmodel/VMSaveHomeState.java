package com.example.musicapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapp.object.Album;
import com.example.musicapp.object.Banner;
import com.example.musicapp.object.MostLikedSong;
import com.example.musicapp.object.PlayList;
import com.example.musicapp.object.PopularSong;
import com.example.musicapp.object.Song;
import com.example.musicapp.object.Type;

import java.util.ArrayList;
import java.util.Map;

public class VMSaveHomeState extends ViewModel {

    private MutableLiveData<Map<String, Integer>> scrollViewPosition;
    private MutableLiveData<ArrayList<Album>> album;
    private MutableLiveData<ArrayList<Banner>> banner;
    private MutableLiveData<ArrayList<Song>> popularSong;
    private MutableLiveData<ArrayList<PlayList>> playList;
    private MutableLiveData<ArrayList<Type>> type;

    public MutableLiveData<Map<String, Integer>> getPosition() {
        if(scrollViewPosition == null) {
            scrollViewPosition = new MutableLiveData<>();
        }
        return scrollViewPosition;
    }
    public MutableLiveData<ArrayList<Album>> getAlbum() {
        if(album == null) {
            album = new MutableLiveData<>();
        }
        return album;
    }
    public MutableLiveData<ArrayList<Banner>> getBanner() {
        if (banner == null) {
            banner = new MutableLiveData<>();
        }
        return banner;
    }
    public MutableLiveData<ArrayList<Song>> getPopularSong() {
        if(popularSong == null) {
            popularSong = new MutableLiveData<>();
        }
        return popularSong;
    }
    public MutableLiveData<ArrayList<PlayList>> getPlayList() {
        if(playList == null) {
            playList = new MutableLiveData<>();
        }
        return playList;
    }
    public MutableLiveData<ArrayList<Type>> getType() {
        if(type == null) {
            type = new MutableLiveData<>();
        }
        return type;
    }
}
