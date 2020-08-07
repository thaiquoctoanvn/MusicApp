package com.example.musicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicapp.adapter.ItemClickListener;
import com.example.musicapp.adapter.LocalListSongAdapter;
import com.example.musicapp.object.PlayList;
import com.example.musicapp.object.PlayListLocal;
import com.example.musicapp.object.Song;
import com.example.musicapp.object.SongLocal;
import com.example.musicapp.object.SongandAlbum;
import com.example.musicapp.viewmodel.VMLoadListSong;
import com.example.musicapp.viewmodel.VMMusicToMiniPlayer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalListSongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalListSongFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton ibtnLocalListSongBack;
    private ImageView ivLocalListSongImage;
    private TextView tvLocalListSongName, tvLocalListSongDetail;
    private RecyclerView rvLocalListSong;
    private LocalListSongAdapter localListSongAdapter;
    private Button btnLocalPlayAllListSong;
    private View view;

    private VMLoadListSong vmLoadListSong;
    private VMMusicToMiniPlayer vmMusicToMiniPlayer;

    private Realm realm;
    private PlayListLocal realmResults;
    private ArrayList<SongLocal> songLocalArrayList;
    private ArrayList<Song> tempList;
    private ArrayList<Song> readyList;
    private String id;

    public LocalListSongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocalListSongFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocalListSongFragment newInstance(String param1, String param2) {
        LocalListSongFragment fragment = new LocalListSongFragment();
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
        view = inflater.inflate(R.layout.fragment_local_list_song, container, false);
        ConnectView();
        return view;
    }

    private void ConnectView() {
        ibtnLocalListSongBack = view.findViewById(R.id.ibtn_locallistsongback);
        ivLocalListSongImage = view.findViewById(R.id.iv_locallistsongimage);
        tvLocalListSongName = view.findViewById(R.id.tv_locallistsongname);
        tvLocalListSongDetail = view.findViewById(R.id.tv_locallistsongdescription);
        rvLocalListSong = view.findViewById(R.id.rv_locallistsong);
        btnLocalPlayAllListSong = view.findViewById(R.id.btn_localplayalllistsong);

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        vmLoadListSong = new ViewModelProvider(getActivity()).get(VMLoadListSong.class);
        vmMusicToMiniPlayer = new ViewModelProvider(getActivity()).get(VMMusicToMiniPlayer.class);

        ibtnLocalListSongBack.setOnClickListener(LocalListSongFragment.this);
        btnLocalPlayAllListSong.setOnClickListener(LocalListSongFragment.this);

        vmLoadListSong.getListSongInfo().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {
                Picasso.get()
                        .load(stringStringMap.get("localimage"))
                        .placeholder(R.mipmap.default_image)
                        .error(R.mipmap.default_image)
                        .into(ivLocalListSongImage);
                tvLocalListSongName.setText(stringStringMap.get("localname"));
                id = stringStringMap.get("locallistid");
                Log.e("local list song", id);
                DisplayListSongLocal();
            }
        });

    }

    private void DisplayListSongLocal() {

        realmResults = realm.where(PlayListLocal.class)
                .equalTo("id", id.trim())
                .findFirst();
        if(realmResults != null) {
            songLocalArrayList = (ArrayList<SongLocal>) realm.copyFromRealm(realmResults.getListSongLocal());
            localListSongAdapter = new LocalListSongAdapter(songLocalArrayList);
            rvLocalListSong.setAdapter(localListSongAdapter);
            rvLocalListSong.setLayoutManager(new LinearLayoutManager(getActivity()));
            tvLocalListSongDetail.setText("Có " + songLocalArrayList.size() + " bài hát");

            tempList = new ArrayList<>();
            Song itemSong = new Song();
            for(SongLocal item : songLocalArrayList) {
                itemSong.setIdSong(item.getId());
                itemSong.setSongName(item.getSongName());
                itemSong.setSongSingerName(item.getSinger());
                itemSong.setSongImage(item.getSongImage());
                itemSong.setSongLink(item.getSongLinkLocal());
                itemSong.setSongLikes("0");
                tempList.add(itemSong);
            }

            localListSongAdapter.SetOnItemClickListener(new ItemClickListener() {
                @Override
                public void OnItemClickListener(View v, int position) {

                    readyList = new ArrayList<>();
                    readyList.add(tempList.get(position));
                    readyList.addAll(tempList);
                    readyList.remove(position);
                    vmMusicToMiniPlayer.getPlayingList().setValue(readyList);
                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ibtn_locallistsongback:
                Navigation.findNavController(v).popBackStack();
                break;
            case R.id.btn_localplayalllistsong:
                if(tempList.size() > 0) {
                    vmMusicToMiniPlayer.getPlayingList().setValue(tempList);
                }
                break;
        }
    }
}
