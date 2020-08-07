package com.example.musicapp;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.adapter.TypeAdapter;
import com.example.musicapp.object.Type;
import com.example.musicapp.service.APIUtil;
import com.example.musicapp.service.RetrofitInterface;
import com.example.musicapp.viewmodel.VMSaveHomeState;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TypeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private RecyclerView rvType;
    private TextView tvMoreType;
    private ProgressBar progressBar;
    private TypeAdapter adapterType;

    private ArrayList<Type> arrType;
    private VMSaveHomeState vmSaveHomeState;

    public TypeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TypeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TypeFragment newInstance(String param1, String param2) {
        TypeFragment fragment = new TypeFragment();
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
        view = inflater.inflate(R.layout.fragment_type, container, false);
        ConnectView();
        vmSaveHomeState = new ViewModelProvider(getActivity()).get(VMSaveHomeState.class);
        vmSaveHomeState.getType().observe(getViewLifecycleOwner(), new Observer<ArrayList<Type>>() {
            @Override
            public void onChanged(ArrayList<Type> arrayList) {
                UpdateUI(arrayList);
            }
        });
        if(vmSaveHomeState.getType().getValue() != null) {
            UpdateUI(vmSaveHomeState.getType().getValue());
        } else {
            GetDataType();
        }
        return view;
    }

    private void ConnectView() {
        rvType = (RecyclerView) view.findViewById(R.id.rv_type);
        tvMoreType = (TextView) view.findViewById(R.id.tv_moretype);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
    }

    private void GetDataType() {
        progressBar.setVisibility(View.VISIBLE);
        arrType = new ArrayList<>();
        RetrofitInterface retrofitInterface = APIUtil.getRetrofitInterface();
        Call<List<Type>> typeCallBack = retrofitInterface.GetDataType();
        typeCallBack.enqueue(new Callback<List<Type>>() {
            @Override
            public void onResponse(Call<List<Type>> call, Response<List<Type>> response) {
                arrType = (ArrayList<Type>) response.body();
                if(arrType != null) {
                    UpdateUI(arrType);
                    vmSaveHomeState.getType().setValue(arrType);
                }
            }

            @Override
            public void onFailure(Call<List<Type>> call, Throwable t) {
                Log.e("###", t.getMessage());
            }
        });
    }

    private void UpdateUI(final ArrayList<Type> arrayList) {
        adapterType = new TypeAdapter(arrayList);
        rvType.setAdapter(adapterType);
        rvType.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        progressBar.setVisibility(View.GONE);
        adapterType.setOnItemListener(new TypeAdapter.TypeItemListener() {
            @Override
            public void onItemClick(View v, int position) {
                Type item = arrayList.get(position);
                Toast.makeText(getActivity(), item.getTypeName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
