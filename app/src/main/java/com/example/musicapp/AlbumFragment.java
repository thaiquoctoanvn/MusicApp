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

import com.example.musicapp.adapter.AlbumAdapter;
import com.example.musicapp.object.Album;
import com.example.musicapp.object.Song;
import com.example.musicapp.service.APIUtil;
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
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView rvAlbum;
    private TextView tvMoreAlbum;
    private ProgressBar progressBar;
    private View view;
    private AlbumAdapter adapterAlbum;
    private ArrayList<Album> arrAlbum;
    private ArrayList<Song> arrListSong;

    private VMSaveHomeState vmSaveHomeState;
    private VMLoadListSong vmLoadListSong;

    public AlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumFragment newInstance(String param1, String param2) {
        AlbumFragment fragment = new AlbumFragment();
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
        view = inflater.inflate(R.layout.fragment_album, container, false);

        ConnectView();

        vmSaveHomeState = new ViewModelProvider(getActivity()).get(VMSaveHomeState.class);
        vmSaveHomeState.getAlbum().observe(getViewLifecycleOwner(), new Observer<ArrayList<Album>>() {
            @Override
            public void onChanged(ArrayList<Album> arrayList) {
                UpdateUI(arrayList);
            }
        });
        if(vmSaveHomeState.getAlbum().getValue() != null) {
            UpdateUI(vmSaveHomeState.getAlbum().getValue());
        } else {
            GetAlbumData();
        }
        return view;
    }

    private void ConnectView() {
        rvAlbum = (RecyclerView) view.findViewById(R.id.rv_album);
        tvMoreAlbum = (TextView) view.findViewById(R.id.tv_morealbum);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
    }

    private void GetAlbumData() {
        progressBar.setVisibility(View.VISIBLE);
        arrAlbum = new ArrayList<>();
        RetrofitInterface retrofitInterface = APIUtil.getRetrofitInterface();
        Call<List<Album>> albumCallBack = retrofitInterface.GetDataAlbum();
        albumCallBack.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                arrAlbum = (ArrayList<Album>) response.body();
                if(arrAlbum != null) {
                    UpdateUI(arrAlbum);
                    vmSaveHomeState.getAlbum().setValue(arrAlbum);
                }
            }
            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Log.e("###", t.getMessage());
            }
        });
    }

    private void UpdateUI(final ArrayList<Album> arrayList) {
        adapterAlbum = new AlbumAdapter(arrayList);
        rvAlbum.setAdapter(adapterAlbum);
        rvAlbum.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        progressBar.setVisibility(View.GONE);
        adapterAlbum.setOnItemAlbumListener(new AlbumAdapter.OnItemAlbumListener() {
            @Override
            public void onItemAlbumClick(View v, int position) {
                Album item = arrayList.get(position);
//                Toast.makeText(getActivity(), item.getAlbumName(), Toast.LENGTH_SHORT).show();
                vmLoadListSong = new ViewModelProvider(getActivity()).get(VMLoadListSong.class);
                GetSongFromAlbum(v, item.getIdAlbum());
                Map<String, String> listSongInfo = new HashMap<>();
                listSongInfo.put("image", item.getAlbumImage());
                listSongInfo.put("name", item.getAlbumName());
                vmLoadListSong.getListSongInfo().setValue(listSongInfo);

            }
        });
    }

    private void GetSongFromAlbum(final View v, String idAlbum) {
        arrListSong = new ArrayList<>();
        RetrofitInterface retrofitInterface = APIUtil.getRetrofitInterface();
        Call<List<Song>> listSongCallBack = retrofitInterface.GetDataSongFromAlbum(idAlbum);
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
