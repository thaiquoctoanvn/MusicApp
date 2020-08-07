package com.example.musicapp.object;







import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlayList {

    @SerializedName("IdPlaylist")
    @Expose
    private String idPlaylist;
    @SerializedName("PlaylistName")
    @Expose
    private String playlistName;
    @SerializedName("PlaylistCover")
    @Expose
    private String playlistCover;
    @SerializedName("PlaylistImage")
    @Expose
    private String playlistImage;

    public String getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(String idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistCover() {
        return playlistCover;
    }

    public void setPlaylistCover(String playlistCover) {
        this.playlistCover = playlistCover;
    }

    public String getPlaylistImage() {
        return playlistImage;
    }

    public void setPlaylistImage(String playlistImage) {
        this.playlistImage = playlistImage;
    }

}