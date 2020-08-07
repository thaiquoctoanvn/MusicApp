package com.example.musicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.adapter.ItemClickListener;
import com.example.musicapp.adapter.ItemLibraryAdapter;
import com.example.musicapp.object.ItemLibrary;
import com.example.musicapp.object.PlayListLocal;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment implements ItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private List<ItemLibrary> libraries;
    private ItemLibraryAdapter itemLibraryAdapter;

    private View view;
    private RecyclerView rvLibraryItem;

    private Realm realm;
    private RealmResults<PlayListLocal> realmResults;

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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
        view = inflater.inflate(R.layout.fragment_library, container, false);
        ConnectView();
        AddLibraryItem();
        return view;
    }

    private void ConnectView() {
        rvLibraryItem = view.findViewById(R.id.rv_libraryitem);
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();
    }

    private void AddLibraryItem() {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmResults = realm.where(PlayListLocal.class).findAll();
            }
        });
        int playlistCounter = realmResults.size();

        libraries = new ArrayList<>();
        ItemLibrary itemPlaylist = new ItemLibrary(
                R.drawable.ic_playlist_play_black_24dp,
                "Playlist",
                String.valueOf(playlistCounter),
                "playlist");
        ItemLibrary itemDownLoad = new ItemLibrary(
                R.drawable.ic_cloud_download_black_24dp,
                "Nhạc đã tải",
                "5",
                "download");
        libraries.add(itemPlaylist);
        libraries.add(itemDownLoad);

        itemLibraryAdapter = new ItemLibraryAdapter(libraries);
        itemLibraryAdapter.notifyDataSetChanged();
        rvLibraryItem.setAdapter(itemLibraryAdapter);
        rvLibraryItem.setLayoutManager(new LinearLayoutManager(getActivity()));

        itemLibraryAdapter.setOnItemClickListener(LibraryFragment.this);
    }

    @Override
    public void OnItemClickListener(View v, int position) {
        ItemLibrary item = libraries.get(position);
        if(item.getItemId().equals("playlist")) {
            Navigation.findNavController(v).navigate(R.id.action_libraryFragment_to_localPlaylistFragment);
        } else if(item.getItemId().equals("download")) {
            Navigation.findNavController(v).navigate(R.id.action_libraryFragment_to_localMusicFragment);
        }
    }
}
