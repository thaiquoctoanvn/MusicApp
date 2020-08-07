package com.example.musicapp;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.adapter.ListSongAdapter;
import com.example.musicapp.object.Song;
import com.example.musicapp.viewmodel.VMLoadListSong;
import com.example.musicapp.viewmodel.VMMusicToMiniPlayer;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListSongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListSongFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private Toolbar toolbarListSong;
    private ImageButton ibtnListSongBack;
    private ImageView ivListSongImage;
    private TextView tvListSongName, tvListSongDescription;
    private Button btnPlayAllList;
    private RecyclerView rvListSong;
    private ListSongAdapter adapterListSong;

    private VMLoadListSong vmLoadListSong;
    private VMMusicToMiniPlayer vmMusicToMiniPlayer;

    private ArrayList<Song> songList;

    public ListSongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListSongFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListSongFragment newInstance(String param1, String param2) {
        ListSongFragment fragment = new ListSongFragment();
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
        view = inflater.inflate(R.layout.fragment_list_song, container, false);
        ConnectView();

        vmLoadListSong = new ViewModelProvider(getActivity()).get(VMLoadListSong.class);
        vmMusicToMiniPlayer = new ViewModelProvider(getActivity()).get(VMMusicToMiniPlayer.class);

        //Lắng nghe sự kiện set danh sách bài hát từ album/playlist
        vmLoadListSong.getListSong().observe(getViewLifecycleOwner(), new Observer<ArrayList<Song>>() {
            @Override
            public void onChanged(ArrayList<Song> songs) {
                UpdateUI(songs);
                songList = new ArrayList<>();
                songList = songs;
            }
        });

        //Lắng nghe sự kiện set hình ảnh và tên album/playlist từ home
        vmLoadListSong.getListSongInfo().observe(getViewLifecycleOwner(), new Observer<Map<String, String>>() {
            @Override
            public void onChanged(Map<String, String> stringStringMap) {
                UpdateListSongInfo(stringStringMap.get("image"), stringStringMap.get("name"));
            }
        });
        return view;
    }

    private void ConnectView() {
        toolbarListSong = view.findViewById(R.id.toolbar_listsong);
        ivListSongImage = view.findViewById(R.id.iv_listsongimage);
        tvListSongName = view.findViewById(R.id.tv_listsongname);
        tvListSongDescription = view.findViewById(R.id.tv_listsongdescription);
        btnPlayAllList = view.findViewById(R.id.btn_playalllist);
        ibtnListSongBack = view.findViewById(R.id.ibtn_listsongback);
        rvListSong = view.findViewById(R.id.rv_listsong);

        ibtnListSongBack.setOnClickListener(ListSongFragment.this);
        btnPlayAllList.setOnClickListener(ListSongFragment.this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ibtn_listsongback:
                Navigation.findNavController(ibtnListSongBack).popBackStack();
                break;
            case R.id.btn_playalllist:
                if(songList.size() > 0) {
                    vmMusicToMiniPlayer.getPlayingList().setValue(songList);
                }
        }
    }

    private void UpdateUI(final ArrayList<Song> arr) {

        tvListSongDescription.setText("Có " + arr.size() + " bài hát");
        adapterListSong = new ListSongAdapter(arr);
        rvListSong.setAdapter(adapterListSong);
        rvListSong.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterListSong.setOnItemSongClickListener(new ListSongAdapter.OnItemSongListener() {
            @Override
            public void OnItemSongClick(View v, int position) {
                Toast.makeText(getActivity(), arr.get(position).getSongName(), Toast.LENGTH_SHORT).show();

//                vmMusicToMiniPlayer.getSongFromListSong().setValue(arr.get(position));
                ArrayList<Song> tempList = new ArrayList<>();
                tempList.add(arr.get(position));
                tempList.addAll(arr);
                tempList.remove(position + 1);
                vmMusicToMiniPlayer.getPlayingList().setValue(tempList);
            }
        });
    }

    //Lấy hình ảnh và tên của album/playlist từ home gắn vào
    private void UpdateListSongInfo(String listSongImage, String listSongName) {
        Picasso.get()
                .load(listSongImage)
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(ivListSongImage);
        tvListSongName.setText(listSongName);
    }
}
