package com.example.musicapp.object;





import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SongandAlbum {

    @SerializedName("Song")
    @Expose
    private List<Song> song = null;
    @SerializedName("Album")
    @Expose
    private List<Album> album = null;

    public List<Song> getSong() {
        return song;
    }

    public void setSong(List<Song> song) {
        this.song = song;
    }

    public List<Album> getAlbum() {
        return album;
    }

    public void setAlbum(List<Album> album) {
        this.album = album;
    }

}
