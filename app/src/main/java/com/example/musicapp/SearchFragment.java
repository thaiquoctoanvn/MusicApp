package com.example.musicapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.musicapp.adapter.RecentKeySearchAdapter;
import com.example.musicapp.object.Album;
import com.example.musicapp.object.RecentKeySearch;
import com.example.musicapp.object.Song;
import com.example.musicapp.object.SongandAlbum;
import com.example.musicapp.service.APIUtil;
import com.example.musicapp.service.RetrofitClient;
import com.example.musicapp.service.RetrofitInterface;
import com.example.musicapp.viewmodel.VMSaveSearchState;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements TextWatcher, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SongandAlbum songandAlbum;
    private ArrayList<Song> tempSongList;
    private ArrayList<Album> temlAlbumList;
    private View view;
    private EditText etSearch;
    private RecyclerView rvRecentSearch;
    private RecentKeySearchAdapter recentKeySearchAdapter;
    private RelativeLayout layoutSearch;

    private VMSaveSearchState vmSaveSearchState;

    private Realm realm;
    private RealmResults<RecentKeySearch> realmResults;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        view = inflater.inflate(R.layout.fragment_search, container, false);
        ConnectView();
        vmSaveSearchState = new ViewModelProvider(getActivity()).get(VMSaveSearchState.class);
        if(vmSaveSearchState.getListSearchSong().getValue() != null) {
            ArrayList<Song> listSongTemp = new ArrayList<>();
            listSongTemp = vmSaveSearchState.getListSearchSong().getValue();
            vmSaveSearchState.getListSearchSong().setValue(listSongTemp);
        }
        if(vmSaveSearchState.getListSearchAlbum().getValue() != null) {
            ArrayList<Album> listAlbumTemp = new ArrayList<>();
            listAlbumTemp = vmSaveSearchState.getListSearchAlbum().getValue();
            vmSaveSearchState.getListSearchAlbum().setValue(listAlbumTemp);
        }
        if(vmSaveSearchState.getKeySearch().getValue() != null) {
            etSearch.setText(vmSaveSearchState.getKeySearch().getValue());
        }
        return view;
    }

    private void ConnectView() {
        etSearch = view.findViewById(R.id.et_search);
        rvRecentSearch = view.findViewById(R.id.rv_recentsearch);
        rvRecentSearch.setNestedScrollingEnabled(false);
        layoutSearch = view.findViewById(R.id.layout_search);
        layoutSearch.setOnClickListener(SearchFragment.this);

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        GetRecentKeySearch();

        etSearch.addTextChangedListener(SearchFragment.this);
    }

    private void GetRecentKeySearch() {
        realmResults = realm.where(RecentKeySearch.class).limit(5).findAll();
        if(realmResults != null) {
            ArrayList<RecentKeySearch> arrayList = (ArrayList<RecentKeySearch>) realm.copyFromRealm(realmResults);
            recentKeySearchAdapter = new RecentKeySearchAdapter(arrayList);
            rvRecentSearch.setAdapter(recentKeySearchAdapter);
            rvRecentSearch.setLayoutManager(new LinearLayoutManager(getActivity()));

        }
    }

    private void GetResultSearch(final String key) {
        songandAlbum = new SongandAlbum();
        RetrofitInterface retrofitInterface = APIUtil.getRetrofitInterface();
        Call<SongandAlbum> resultCallBack = retrofitInterface.GetDataSongandAlbum(key);
        resultCallBack.enqueue(new Callback<SongandAlbum>() {
            @Override
            public void onResponse(Call<SongandAlbum> call, Response<SongandAlbum> response) {
                songandAlbum = (SongandAlbum) response.body();

                if ((songandAlbum != null)) {
                    tempSongList = new ArrayList<>();
                    tempSongList = (ArrayList<Song>) songandAlbum.getSong();

                    temlAlbumList = new ArrayList<>();
                    temlAlbumList = (ArrayList<Album>) songandAlbum.getAlbum();
                    if(tempSongList != null) {
                        vmSaveSearchState.getListSearchSong().setValue(tempSongList);
                    }
                    if(temlAlbumList != null) {
                        vmSaveSearchState.getListSearchAlbum().setValue(temlAlbumList);
                    }


                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RecentKeySearch item = new RecentKeySearch();
                            item.setKeys(etSearch.getText().toString());
                            realm.copyToRealm(item);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SongandAlbum> call, Throwable t) {
                Log.e("###", t.getMessage());
            }
        });
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String words = s.toString().trim();
        vmSaveSearchState.getKeySearch().setValue(words);
        if(words != "") {
            rvRecentSearch.setVisibility(View.GONE);

        } else {
            rvRecentSearch.setVisibility(View.VISIBLE);
        }
        GetResultSearch(s.toString());
    }

    private void HideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getApplicationWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.layout_search:
                HideKeyBoard();
                break;
        }
    }
}
