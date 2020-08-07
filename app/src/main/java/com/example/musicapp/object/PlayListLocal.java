package com.example.musicapp.object;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PlayListLocal extends RealmObject {
    @PrimaryKey
    String id;
    String playlistName;
    String playlistImage;
    RealmList<SongLocal> listSongLocal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public RealmList<SongLocal> getListSongLocal() {
        return listSongLocal;
    }

    public void setListSongLocal(RealmList<SongLocal> listSongLocal) {
        this.listSongLocal = listSongLocal;
    }

    public String getPlaylistImage() {
        return playlistImage;
    }

    public void setPlaylistImage(String playlistImage) {
        this.playlistImage = playlistImage;
    }
}
