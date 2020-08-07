package com.example.musicapp.object;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SongLocal extends RealmObject {
    @PrimaryKey
    String id;
    String songName;
    String singer;
    String songImage;
    String songLinkLocal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public String getSongLinkLocal() {
        return songLinkLocal;
    }

    public void setSongLinkLocal(String songLinkLocal) {
        this.songLinkLocal = songLinkLocal;
    }
}
