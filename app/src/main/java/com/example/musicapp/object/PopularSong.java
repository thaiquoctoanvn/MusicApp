package com.example.musicapp.object;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PopularSong {

    @SerializedName("IdSong")
    @Expose
    private String idSong;
    @SerializedName("SongName")
    @Expose
    private String songName;
    @SerializedName("SongImage")
    @Expose
    private String songImage;
    @SerializedName("SongSingerName")
    @Expose
    private String songSingerName;
    @SerializedName("SongLink")
    @Expose
    private String songLink;
    @SerializedName("SongLikes")
    @Expose
    private String songLikes;

    public String getIdSong() {
        return idSong;
    }

    public void setIdSong(String idSong) {
        this.idSong = idSong;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public String getSongSingerName() {
        return songSingerName;
    }

    public void setSongSingerName(String songSingerName) {
        this.songSingerName = songSingerName;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getSongLikes() {
        return songLikes;
    }

    public void setSongLikes(String songLikes) {
        this.songLikes = songLikes;
    }

}
