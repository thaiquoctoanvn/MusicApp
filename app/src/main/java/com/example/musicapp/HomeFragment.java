package com.example.musicapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.example.musicapp.viewmodel.VMSaveHomeState;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private ScrollView scrollView;
    private ImageButton ibtnSetting;

    private Map<String, Integer> map;
//    private ViewModelHome viewModelHome;
    private VMSaveHomeState vmSaveHomeState;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        view = inflater.inflate(R.layout.fragment_home, container, false);
        scrollView = view.findViewById(R.id.sv_home);
        ibtnSetting = view.findViewById(R.id.ibtn_setting);
        vmSaveHomeState = new ViewModelProvider(getActivity()).get(VMSaveHomeState.class);

        vmSaveHomeState.getPosition().observe(getViewLifecycleOwner(), new Observer<Map<String, Integer>>() {
            @Override
            public void onChanged(final Map<String, Integer> stringFloatMap) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(stringFloatMap.get("xx"),  stringFloatMap.get("yy"));
                    }
                });
            }
        });
//        if(vmSaveHomeState.getPosition().getValue() != null) {
//            map = vmSaveHomeState.getPosition().getValue();
//            ViewTreeObserver vto = scrollView.getViewTreeObserver();
//            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                public void onGlobalLayout() {
//                    scrollView.scrollTo(map.get("xx"), map.get("yy"));
//                }
//            });
//
//        }

        ibtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_settingFragment);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        Map<String, Integer> integerMap = new HashMap<>();
        integerMap.put("xx", scrollView.getScrollX());
        integerMap.put("yy", scrollView.getScrollY());
        vmSaveHomeState.getPosition().setValue(integerMap);
        Log.e("bbb", String.valueOf(integerMap.get("xx")));
        Log.e("bbb", String.valueOf(integerMap.get("yy")));
    }

}
