package com.example.musicapp.object;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Banner {

    @SerializedName("IdBanner")
    @Expose
    private String idBanner;
    @SerializedName("BannerImage")
    @Expose
    private String bannerImage;
    @SerializedName("BannerDescription")
    @Expose
    private String bannerDescription;
    @SerializedName("IdSong")
    @Expose
    private String idSong;
    @SerializedName("SongName")
    @Expose
    private String songName;
    @SerializedName("SongSinger")
    @Expose
    private String songSinger;
    @SerializedName("SongImage")
    @Expose
    private String songImage;
    @SerializedName("SongLink")
    @Expose
    private String songLink;

    public String getIdBanner() {
        return idBanner;
    }

    public void setIdBanner(String idBanner) {
        this.idBanner = idBanner;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getBannerDescription() {
        return bannerDescription;
    }

    public void setBannerDescription(String bannerDescription) {
        this.bannerDescription = bannerDescription;
    }

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

    public String getSongSinger() {
        return songSinger;
    }

    public void setSongSinger(String songSinger) {
        this.songSinger = songSinger;
    }

    public String getSongImage() {
        return songImage;
    }

    public void setSongImage(String songImage) {
        this.songImage = songImage;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

}
