package com.example.musicapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.adapter.ItemClickListener;
import com.example.musicapp.adapter.LocalPlaylistAdapter;
import com.example.musicapp.adapter.SwipeToDeleteCallback;
import com.example.musicapp.object.PlayListLocal;
import com.example.musicapp.viewmodel.VMLoadListSong;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalPlaylistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalPlaylistFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton ibtnAddLocalPlaylist, ibtnBackLocalPlaylist;
    private RecyclerView rvLocalPlaylist;
    private LocalPlaylistAdapter localPlaylistAdapter;
    private View view;
    private android.app.Dialog dialogAdd;
    private EditText etPlaylistName;
    private Button btnAdd, btnCancel;

    private String imageLink = "";
    private CharSequence [] optionsDialog = {"Chọn từ thư viện", "Đóng"};
    private final int REQUEST_CODE_CAMERA = 1;
    private final int REQUEST_CODE_GALLERY = 2;
    private ArrayList<PlayListLocal> listLocals;

    private Realm realm;
    private RealmResults<PlayListLocal> realmResults;
    private VMLoadListSong vmLoadListSong;

    private ItemTouchHelper itemTouchHelper;

    public LocalPlaylistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocalPlaylistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocalPlaylistFragment newInstance(String param1, String param2) {
        LocalPlaylistFragment fragment = new LocalPlaylistFragment();
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
        view = inflater.inflate(R.layout.fragment_local_playlist, container, false);
        ConnectView();
        return view;
    }

    private void ConnectView() {
        ibtnAddLocalPlaylist = view.findViewById(R.id.ibtn_addlocalplaylist);
        ibtnBackLocalPlaylist = view.findViewById(R.id.ibtn_localplaylistback);
        rvLocalPlaylist = view.findViewById(R.id.rv_localplaylist);

        vmLoadListSong = new ViewModelProvider(getActivity()).get(VMLoadListSong.class);

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();

        DisplayLocalPlaylist();

        itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(LocalPlaylistFragment.this));
        itemTouchHelper.attachToRecyclerView(rvLocalPlaylist);

        ibtnAddLocalPlaylist.setOnClickListener(LocalPlaylistFragment.this);
        ibtnBackLocalPlaylist.setOnClickListener(LocalPlaylistFragment.this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ibtn_localplaylistback:
                Navigation.findNavController(ibtnBackLocalPlaylist).popBackStack();
                break;
            case R.id.ibtn_addlocalplaylist:
                ShowDialogAddPlaylist();
                break;
            case R.id.btn_cancel:
                dialogAdd.dismiss();
                break;
            case R.id.btn_add:
                AddPlaylist();
                break;
        }

    }

    private void DisplayLocalPlaylist() {
        realmResults = realm.where(PlayListLocal.class)
                .sort("playlistName", Sort.ASCENDING)
                .findAll();
        listLocals = new ArrayList<>();
        listLocals = (ArrayList<PlayListLocal>) realm.copyFromRealm(realmResults);
        localPlaylistAdapter = new LocalPlaylistAdapter(listLocals);
        rvLocalPlaylist.setAdapter(localPlaylistAdapter);
        rvLocalPlaylist.setLayoutManager(new LinearLayoutManager(getActivity()));

//        realmResults.addChangeListener(callback);

        localPlaylistAdapter.SetOnLocalListItemClickListener(new ItemClickListener() {
            @Override
            public void OnItemClickListener(View v, int position) {
                Navigation.findNavController(v).navigate(R.id.action_localPlaylistFragment_to_localListSongFragment);
                Map<String, String> localPlaylistInfo = new HashMap<>();
                localPlaylistInfo.put("localimage", listLocals.get(position).getPlaylistImage());
                localPlaylistInfo.put("localname", listLocals.get(position).getPlaylistName());
                localPlaylistInfo.put("locallistid", listLocals.get(position).getId());
                Log.e("local playlist", listLocals.get(position).getId());
                vmLoadListSong.getListSongInfo().setValue(localPlaylistInfo);
            }
        });
    }


    private void ShowDialogAddPlaylist() {
        dialogAdd = new Dialog(getActivity());
        dialogAdd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAdd.setContentView(R.layout.item_dialogaddlocalplaylist);
        dialogAdd.setCanceledOnTouchOutside(false);

        etPlaylistName = dialogAdd.findViewById(R.id.et_localplaylistname);
        btnAdd = dialogAdd.findViewById(R.id.btn_add);
        btnCancel = dialogAdd.findViewById(R.id.btn_cancel);

        btnAdd.setOnClickListener(LocalPlaylistFragment.this);
        btnCancel.setOnClickListener(LocalPlaylistFragment.this);

        dialogAdd.show();
    }

    private void AddPlaylist() {
        if(imageLink == "") {
            TakeImageFrom();
            Log.e("###", "Image null");
        } else {
            AddToLocal();
            Log.e("###", "Image not null");
        }
    }

    private void AddToLocal() {
        final String playlistName = etPlaylistName.getText().toString().trim();
        if(playlistName == "") {
            Toast.makeText(getActivity(), "Hãy nhập têm playlist", Toast.LENGTH_SHORT).show();
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    PlayListLocal item = new PlayListLocal();
                    item.setPlaylistName(playlistName);
                    item.setId(String.format("%d.jpg", System.currentTimeMillis()) + playlistName);
                    item.setPlaylistImage(imageLink);
                    item.setListSongLocal(null);

                    realm.copyToRealm(item);

                    dialogAdd.dismiss();
                }
            });
            realmResults = realm.where(PlayListLocal.class)
                    .sort("playlistName", Sort.ASCENDING)
                    .findAll();
            listLocals.clear();
            listLocals.addAll(realm.copyFromRealm(realmResults));
            localPlaylistAdapter.notifyDataSetChanged();
        }
    }

    public void DeletePlayList(final int position) {
        final PlayListLocal recentDeletedItem = listLocals.get(position);
        listLocals.remove(position);
        localPlaylistAdapter.notifyItemRemoved(position);
        final PlayListLocal targetItem = realm.where(PlayListLocal.class)
                .equalTo("id", recentDeletedItem.getId())
                .findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                targetItem.deleteFromRealm();
                UndoDeleteItem(recentDeletedItem, position);
            }
        });
    }

    private void UndoDeleteItem(final PlayListLocal item, final int itemPosition) {
        Snackbar snackbar = Snackbar.make(view.findViewById(R.id.forSnackBar), "Đã xóa mục", Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.setAction("Hoàn tác", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listLocals.add(item);
                localPlaylistAdapter.notifyItemInserted(itemPosition);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(item);
                    }
                });
            }
        });
        snackbar.show();
    }

    private void TakeImageFrom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thêm ảnh cho playlist");
        builder.setItems(optionsDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(optionsDialog[which].equals("Chọn từ thư viện")) {
                    requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_GALLERY);
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode) {
//            case REQUEST_CODE_CAMERA:
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAMERA);
//                }
//                break;
            case REQUEST_CODE_GALLERY:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_GALLERY);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            imageLink = uri.toString();
            Log.e("###", imageLink);
            btnAdd.setText("Thêm playlist");
        }

    }

//    private OrderedRealmCollectionChangeListener<RealmResults<PlayListLocal>> callback = new OrderedRealmCollectionChangeListener<RealmResults<PlayListLocal>>() {
//        @Override
//        public void onChange(RealmResults<PlayListLocal> playListLocals, OrderedCollectionChangeSet changeSet) {
//            if(changeSet == null) {
//
//            } else {
//                listLocals.clear();
//                listLocals = (ArrayList<PlayListLocal>) realm.copyFromRealm(playListLocals);
//                localPlaylistAdapter.notifyDataSetChanged();
//            }
//        }
//    };
}
