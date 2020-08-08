package com.example.musicapp;

import android.content.Context;
import android.hardware.input.InputManager;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.musicapp.adapter.ItemClickListener;
import com.example.musicapp.adapter.SearchAlbumAdapter;
import com.example.musicapp.adapter.SearchSongAdapter;
import com.example.musicapp.object.Album;
import com.example.musicapp.object.Song;
import com.example.musicapp.service.APIUtil;
import com.example.musicapp.service.RetrofitInterface;
import com.example.musicapp.viewmodel.VMLoadListSong;
import com.example.musicapp.viewmodel.VMMusicToMiniPlayer;
import com.example.musicapp.viewmodel.VMSaveSearchState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchResultFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Song> arrFromAlbum;

    private View view;
    private RelativeLayout layoutResultSong, layoutResultAlbum;
    private TextView tvMoreSearchSong, tvMoreSearchAlbum;
    private RecyclerView rvSearchSong, rvSearchAlbum;
    private SearchSongAdapter searchSongAdapter;
    private SearchAlbumAdapter searchAlbumAdapter;
    private RelativeLayout layoutSearchResult;

    private VMSaveSearchState vmSaveSearchState;
    private VMMusicToMiniPlayer vmMusicToMiniPlayer;
    private VMLoadListSong vmLoadListSong;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultFragment newInstance(String param1, String param2) {
        SearchResultFragment fragment = new SearchResultFragment();
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
        view = inflater.inflate(R.layout.fragment_search_result, container, false);
        ConnectView();
        vmSaveSearchState = new ViewModelProvider(getActivity()).get(VMSaveSearchState.class);
        vmMusicToMiniPlayer = new ViewModelProvider(getActivity()).get(VMMusicToMiniPlayer.class);
        vmLoadListSong = new ViewModelProvider(getActivity()).get(VMLoadListSong.class);

        vmSaveSearchState.getListSearchSong().observe(getViewLifecycleOwner(), new Observer<ArrayList<Song>>() {
            @Override
            public void onChanged(final ArrayList<Song> songs) {
                if(songs.size() > 0) {
                    layoutResultSong.setVisibility(View.VISIBLE);
                    searchSongAdapter = new SearchSongAdapter(songs);
                    rvSearchSong.setAdapter(searchSongAdapter);
                    rvSearchSong.setLayoutManager(new LinearLayoutManager(getActivity()));

                    searchSongAdapter.SetOnItemClickListener(new ItemClickListener() {
                        @Override
                        public void OnItemClickListener(View v, int position) {
                            ArrayList<Song> tempList = new ArrayList<>();
                            tempList.add(songs.get(position));
                            tempList.addAll(songs);
                            tempList.remove(position);
                            vmMusicToMiniPlayer.getPlayingList().setValue(tempList);
                            HideKeyBoard();
                        }
                    });
                }
            }
        });

        vmSaveSearchState.getListSearchAlbum().observe(getViewLifecycleOwner(), new Observer<ArrayList<Album>>() {
            @Override
            public void onChanged(final ArrayList<Album> arrayList) {
                if(arrayList.size() > 0) {
                    layoutResultAlbum.setVisibility(View.VISIBLE);
                    searchAlbumAdapter = new SearchAlbumAdapter(arrayList);
                    rvSearchAlbum.setAdapter(searchAlbumAdapter);
                    rvSearchAlbum.setLayoutManager(new LinearLayoutManager(getActivity()));

                    searchAlbumAdapter.SetOnItemClickListener(new ItemClickListener() {
                        @Override
                        public void OnItemClickListener(View v, int position) {
                            Album item = arrayList.get(position);
                            GetSongFromAlbum(v, item.getIdAlbum());
                            Map<String, String> listSongInfo = new HashMap<>();
                            listSongInfo.put("image", item.getAlbumImage());
                            listSongInfo.put("name", item.getAlbumName());
                            vmLoadListSong.getListSongInfo().setValue(listSongInfo);
                        }
                    });
                }
            }
        });
        return view;
    }

    private void ConnectView() {
        tvMoreSearchSong = view.findViewById(R.id.tv_moresearchsong);
        tvMoreSearchAlbum = view.findViewById(R.id.tv_moresearchalbum);
        rvSearchSong = view.findViewById(R.id.rv_searchsong);
        rvSearchAlbum = view.findViewById(R.id.rv_searchalbum);
        layoutResultSong = view.findViewById(R.id.layout_resultsong);
        layoutResultAlbum = view.findViewById(R.id.layout_resultalbum);
        layoutSearchResult = view.findViewById(R.id.layout_searchresult);

        layoutSearchResult.setOnClickListener(SearchResultFragment.this);

    }

    private void GetSongFromAlbum(final View v, String idAlbum) {
        arrFromAlbum = new ArrayList<>();
        RetrofitInterface retrofitInterface = APIUtil.getRetrofitInterface();
        Call<List<Song>> listSongCallBack = retrofitInterface.GetDataSongFromAlbum(idAlbum);
        listSongCallBack.enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if(response != null) {
                    arrFromAlbum = (ArrayList<Song>) response.body();
                    vmLoadListSong.getListSong().setValue(arrFromAlbum);
                    Navigation.findNavController(v).navigate(R.id.action_searchFragment_to_listSongFragment);
                }
            }
            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                Log.e("###", t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.layout_searchresult:
                HideKeyBoard();
                break;
        }
    }

    private void HideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getApplicationWindowToken(), 0);
    }
}
