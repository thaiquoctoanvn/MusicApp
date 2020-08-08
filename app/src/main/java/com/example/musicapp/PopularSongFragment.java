package com.example.musicapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.adapter.ItemClickListener;
import com.example.musicapp.adapter.LocalPlaylistAdapter;
import com.example.musicapp.adapter.PopularSongAdapter;
import com.example.musicapp.object.PlayListLocal;
import com.example.musicapp.object.PopularSong;
import com.example.musicapp.object.Song;
import com.example.musicapp.object.SongLocal;
import com.example.musicapp.service.APIUtil;
import com.example.musicapp.service.RetrofitInterface;
import com.example.musicapp.viewmodel.VMMusicToMiniPlayer;
import com.example.musicapp.viewmodel.VMSaveHomeState;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopularSongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopularSongFragment extends Fragment implements PopularSongAdapter.AddSongToLocalListListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tvMoreSong;
    private ProgressBar progressBar;
    private View view;
    private RecyclerView rvPopularSong;
    private PopularSongAdapter popularSongAdapter;
    private Dialog dialogLocalListOption;
    private RecyclerView rvLocalListOption;
    private LocalPlaylistAdapter localPlaylistAdapter;

    private VMSaveHomeState vmSaveHomeState;
    private VMMusicToMiniPlayer vmMusicToMiniPlayer;

    private ArrayList<Song> arrPopularSong;
    private ArrayList<PlayListLocal> optionsListDialog;

    private Realm realm;
    private RealmResults<PlayListLocal> realmResults;

    public PopularSongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopularSongFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopularSongFragment newInstance(String param1, String param2) {
        PopularSongFragment fragment = new PopularSongFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_popular_song, container, false);
        ConnectView();
        vmSaveHomeState = new ViewModelProvider(getActivity()).get(VMSaveHomeState.class);
        vmSaveHomeState.getPopularSong().observe(getViewLifecycleOwner(), new Observer<ArrayList<Song>>() {
            @Override
            public void onChanged(ArrayList<Song> arrayList) {
                UpdateUI(arrayList);
            }
        });
        if(vmSaveHomeState.getPopularSong().getValue() != null) {
            UpdateUI(vmSaveHomeState.getPopularSong().getValue());
        } else {
            GetMostLikedSongData();
        }
        return view;
    }

    private void ConnectView() {
        rvPopularSong = view.findViewById(R.id.rv_popularsong);
        tvMoreSong = view.findViewById(R.id.tv_morepopularsong);
        progressBar = view.findViewById(R.id.progressbar);
        rvPopularSong.setNestedScrollingEnabled(false);

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();
        realmResults = realm.where(PlayListLocal.class)
                .sort("playlistName", Sort.ASCENDING)
                .findAll();
        optionsListDialog = new ArrayList<>();
        optionsListDialog = (ArrayList<PlayListLocal>) realm.copyFromRealm(realmResults);
    }

    private void GetMostLikedSongData() {
        progressBar.setVisibility(View.VISIBLE);
        arrPopularSong = new ArrayList<>();
        RetrofitInterface retrofitInterface = APIUtil.getRetrofitInterface();
        Call<List<Song>> likedSongCallBack = retrofitInterface.GetDataPopularSong();
        likedSongCallBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                arrPopularSong = (ArrayList<Song>) response.body();
                if(arrPopularSong != null) {
                    UpdateUI(arrPopularSong);

                    vmSaveHomeState.getPopularSong().setValue(arrPopularSong);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("###", t.getMessage());
            }
        });
    }

    private void UpdateUI(final ArrayList<Song> arrayList) {

        popularSongAdapter = new PopularSongAdapter(arrayList);
        rvPopularSong.setAdapter(popularSongAdapter);
        rvPopularSong.setLayoutManager(new LinearLayoutManager(getActivity()));
        progressBar.setVisibility(View.GONE);

        popularSongAdapter.setOnItemPopularSongClickListener(new PopularSongAdapter.OnItemPopularSongListener() {
            @Override
            public void onItemPopularSongClick(View v, int position) {
//                Toast.makeText(getActivity(), arrayList.get(position).getSongName(), Toast.LENGTH_SHORT).show();
                vmMusicToMiniPlayer = new ViewModelProvider(getActivity()).get(VMMusicToMiniPlayer.class);
                //Set dữ liệu popularsong vào miniplayer
//                vmMusicToMiniPlayer.getSongFromListSong().setValue(arrayList.get(position));
                ArrayList<Song> tempList = new ArrayList<>();
                tempList.add(arrayList.get(position));
                tempList.addAll(arrayList);
                tempList.remove(position + 1);
                vmMusicToMiniPlayer.getPlayingList().setValue(tempList);
            }
        });

        popularSongAdapter.AddToLocalListClickListener(PopularSongFragment.this);
    }

    @Override
    public void addToLocalListClick(View v, int positionPopularSong) {

        dialogLocalListOption = new Dialog(getActivity());
        dialogLocalListOption.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLocalListOption.setContentView(R.layout.item_locallistoptiondialog);
        dialogLocalListOption.setCanceledOnTouchOutside(true);

        rvLocalListOption = dialogLocalListOption.findViewById(R.id.rv_locallistoption);
        localPlaylistAdapter = new LocalPlaylistAdapter(optionsListDialog);
        rvLocalListOption.setAdapter(localPlaylistAdapter);
        rvLocalListOption.setLayoutManager(new LinearLayoutManager(getActivity()));

        dialogLocalListOption.show();
        final Song song = arrPopularSong.get(positionPopularSong);
        localPlaylistAdapter.SetOnLocalListItemClickListener(new ItemClickListener() {
            @Override
            public void OnItemClickListener(View v, final int position) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        PlayListLocal playListLocal = optionsListDialog.get(position);
                        PlayListLocal item = realm.where(PlayListLocal.class).equalTo("id", playListLocal.getId()).findFirst();
                        SongLocal songLocal = new SongLocal();
                        songLocal.setId(String.format("%d.jpg", System.currentTimeMillis()) + song.getSongName());
                        songLocal.setSongName(song.getSongName());
                        songLocal.setSinger(song.getSongSingerName());
                        songLocal.setSongImage(song.getSongImage());
                        songLocal.setSongLinkLocal(song.getSongLink());

                        item.getListSongLocal().add(songLocal);

                        realm.copyToRealmOrUpdate(item);

                        dialogLocalListOption.dismiss();
                        Toast.makeText(getActivity(), "Đã thêm vào playlist", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}