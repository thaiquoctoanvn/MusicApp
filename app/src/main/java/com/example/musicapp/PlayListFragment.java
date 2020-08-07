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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.adapter.PlayListAdapter;
import com.example.musicapp.object.PlayList;

import com.example.musicapp.object.Song;
import com.example.musicapp.service.APIUtil;
import com.example.musicapp.service.RetrofitClient;
import com.example.musicapp.service.RetrofitInterface;
import com.example.musicapp.viewmodel.VMLoadListSong;
import com.example.musicapp.viewmodel.VMSaveHomeState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private TextView tvMore;
    private RecyclerView rvPlayList;
    private ProgressBar progressBar;
    private PlayListAdapter playListAdapter;
    private Thread thread;

    private ArrayList<PlayList> arrPlaylist;
    private ArrayList<Song> arrListSong;

    private VMSaveHomeState vmSaveHomeState;
    private VMLoadListSong vmLoadListSong;


    public PlayListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayListFragment newInstance(String param1, String param2) {
        PlayListFragment fragment = new PlayListFragment();
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
        view = inflater.inflate(R.layout.fragment_play_list, container, false);

        ConnectView();
        vmSaveHomeState = new ViewModelProvider(getActivity()).get(VMSaveHomeState.class);
        vmSaveHomeState.getPlayList().observe(getViewLifecycleOwner(), new Observer<ArrayList<PlayList>>() {
            @Override
            public void onChanged(ArrayList<PlayList> arrayList) {
                UpdateUI(arrayList);
            }
        });
        if(vmSaveHomeState.getPlayList().getValue() != null) {
            UpdateUI(vmSaveHomeState.getPlayList().getValue());
//            Log.e("bbb", "Load from playlist viewmodel");
        } else {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    GetPlayListData();
                }
            });
            thread.start();
        }

        return view;
    }

    private void ConnectView() {
        tvMore = (TextView) view.findViewById(R.id.tv_moreplaylist);
        rvPlayList = (RecyclerView) view.findViewById(R.id.rv_playlist);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        //LoadProgressBar loadProgressBar = new LoadProgressBar(getActivity());
        //loadProgressBar.showProgressBar();
    }

    private void GetPlayListData() {
        progressBar.setVisibility(View.VISIBLE);
        arrPlaylist = new ArrayList<>();
        RetrofitInterface retrofitInterface = APIUtil.getRetrofitInterface();
        Call<List<PlayList>> playListCallBack = retrofitInterface.GetDataPlayList();

        playListCallBack.enqueue(new Callback<List<PlayList>>() {
            @Override
            public void onResponse(Call<List<PlayList>> call, Response<List<PlayList>> response) {
                arrPlaylist = (ArrayList<PlayList>) response.body();
                if(arrPlaylist != null) {
                    UpdateUI(arrPlaylist);
                    vmSaveHomeState.getPlayList().setValue(arrPlaylist);
                }
            }

            @Override
            public void onFailure(Call<List<PlayList>> call, Throwable t) {
                Log.e("###", t.getMessage());
            }
        });
    }

    private void UpdateUI(final ArrayList<PlayList> arrayList) {
        if(thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        playListAdapter = new PlayListAdapter(arrayList);
        rvPlayList.setAdapter(playListAdapter);
        rvPlayList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        progressBar.setVisibility(View.GONE);

        playListAdapter.setOnItemListener(new PlayListAdapter.recyclerviewItemListener() {
            @Override
            public void onItemListener(View view, int position) {
                PlayList item = arrayList.get(position);
                vmLoadListSong = new ViewModelProvider(getActivity()).get(VMLoadListSong.class);
                GetSongFromPlaylist(view, item.getIdPlaylist());

                Map<String, String> listSongInfo = new HashMap<>();
                listSongInfo.put("image", item.getPlaylistImage());
                listSongInfo.put("name", item.getPlaylistName());
                vmLoadListSong.getListSongInfo().setValue(listSongInfo);
            }
        });
    }

    //Load bài hát của playlist về và nhảy sang fragment listsong
    private void GetSongFromPlaylist(final View v, String idPlaylist) {
        arrListSong = new ArrayList<>();
        RetrofitInterface retrofitInterface = APIUtil.getRetrofitInterface();
        Call<List<Song>> listSongCallBack = retrofitInterface.GetDataSongFromPlaylist(idPlaylist);
        listSongCallBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if(response != null) {
                    arrListSong = (ArrayList<Song>) response.body();


                    vmLoadListSong.getListSong().setValue(arrListSong);
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_listSongFragment);
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("###", t.getMessage());
            }
        });
    }
}
