package com.example.musicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.musicapp.adapter.DownloadedSongAdapter;
import com.example.musicapp.adapter.ItemClickListener;
import com.example.musicapp.object.DownloadedSong;
import com.example.musicapp.object.Song;
import com.example.musicapp.viewmodel.VMMusicToMiniPlayer;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalMusicFragment extends Fragment implements View.OnClickListener, ItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton ibtnLocalMusicBack;
    private View view;
    private RecyclerView rvDownloadedSong;
    private DownloadedSongAdapter adapterDownloadedSong;

    private Realm realm;
    private List<Song> songList;
    private RealmResults<DownloadedSong> realmResultsDownloadedSong;
    private VMMusicToMiniPlayer vmMusicToMiniPlayer;

    public LocalMusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocalMusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocalMusicFragment newInstance(String param1, String param2) {
        LocalMusicFragment fragment = new LocalMusicFragment();
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
        view = inflater.inflate(R.layout.fragment_local_music, container, false);
        vmMusicToMiniPlayer = new ViewModelProvider(getActivity()).get(VMMusicToMiniPlayer.class);
        ConnectView();
        DisplayDownloadedSong();
        return view;
    }

    private void ConnectView() {
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        ibtnLocalMusicBack = view.findViewById(R.id.ibtn_localmusicback);
        rvDownloadedSong = (RecyclerView) view.findViewById(R.id.rv_downloadsong);
        ibtnLocalMusicBack.setOnClickListener(LocalMusicFragment.this);
    }

    private void DisplayDownloadedSong() {
        realmResultsDownloadedSong = realm.where(DownloadedSong.class).sort("songName", Sort.ASCENDING).findAll();
        List<DownloadedSong> tempList = realm.copyFromRealm(realmResultsDownloadedSong);

        songList = new ArrayList<>();
        for(DownloadedSong item : tempList) {
            Song song = new Song();
            song.setIdSong(item.getId());
            song.setSongName(item.getSongName());
            song.setSongSingerName(item.getSinger());
            song.setSongLink(item.getSongLinkLocal());
            song.setSongImage(item.getSongImage());
            song.setSongLikes("0");
            songList.add(song);
        }
        adapterDownloadedSong = new DownloadedSongAdapter(songList);
        rvDownloadedSong.setAdapter(adapterDownloadedSong);
        rvDownloadedSong.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterDownloadedSong.SetOnItemClickListener(LocalMusicFragment.this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ibtn_localmusicback:
                Navigation.findNavController(v).popBackStack();
        }
    }

    @Override
    public void OnItemClickListener(View v, int position) {
        ArrayList<Song> tempList = new ArrayList<>();
        tempList.add(songList.get(position));
        tempList.addAll(songList);
        tempList.remove(position + 1);
        vmMusicToMiniPlayer.getPlayingList().setValue(tempList);
    }
}
