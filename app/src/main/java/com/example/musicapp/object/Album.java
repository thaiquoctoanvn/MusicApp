package com.example.musicapp.object;







import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Album {

    @SerializedName("IdAlbum")
    @Expose
    private String idAlbum;
    @SerializedName("AlbumName")
    @Expose
    private String albumName;
    @SerializedName("SingerName")
    @Expose
    private String singerName;
    @SerializedName("AlbumImage")
    @Expose
    private String albumImage;

    public String getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(String idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

}