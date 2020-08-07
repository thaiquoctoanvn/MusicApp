package com.example.musicapp.service;

import com.example.musicapp.object.Album;
import com.example.musicapp.object.Banner;
import com.example.musicapp.object.MostLikedSong;
import com.example.musicapp.object.PlayList;
import com.example.musicapp.object.PopularSong;
import com.example.musicapp.object.Song;
import com.example.musicapp.object.SongandAlbum;
import com.example.musicapp.object.Type;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("songbanner.php")
    Call<List<Banner>> GetDataBanner();

    @POST("currentplaylist.php")
    Call<List<PlayList>> GetDataPlayList();

    @POST("musictype.php")
    Call<List<Type>> GetDataType();

    @POST("album.php")
    Call<List<Album>> GetDataAlbum();

    @POST("popularsong.php")
    Call<List<Song>> GetDataPopularSong();

    @FormUrlEncoded
    @POST("songfromplaylist.php")
    Call<List<Song>> GetDataSongFromPlaylist(@Field("idPlaylist") String idplaylist);

    @FormUrlEncoded
    @POST("songfromalbum.php")
    Call<List<Song>> GetDataSongFromAlbum(@Field("idAlbum") String idalbum);

    @FormUrlEncoded
    @POST("search.php")
    Call<SongandAlbum> GetDataSongandAlbum(@Field("Key") String key);
}
