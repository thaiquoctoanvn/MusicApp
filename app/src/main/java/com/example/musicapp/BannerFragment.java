package com.example.musicapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.musicapp.adapter.BannerAdapterUpgrade;
import com.example.musicapp.object.Banner;
import com.example.musicapp.object.Song;
import com.example.musicapp.service.APIUtil;
import com.example.musicapp.service.RetrofitInterface;
import com.example.musicapp.viewmodel.VMMusicToMiniPlayer;
import com.example.musicapp.viewmodel.VMSaveHomeState;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BannerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    //private ViewPager viewPager;
    private ViewPager2 viewPager;
    private CircleIndicator3 circleIndicator3;
    //private BannerAdapter bannerAdapter;
    private BannerAdapterUpgrade bannerAdapterUpgrade;
    private Runnable runnable;
    private Handler handler;
    private int currentItem;
    private ProgressBar progressBar;

    private ArrayList<Banner> arrBanner;

    private VMSaveHomeState vmSaveHomeState;
    private VMMusicToMiniPlayer vmMusicToMiniPlayer;


    public BannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BannerFragment newInstance(String param1, String param2) {
        BannerFragment fragment = new BannerFragment();
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
        view = inflater.inflate(R.layout.fragment_banner, container, false);
        ConnectView();

        vmSaveHomeState = new ViewModelProvider(getActivity()).get(VMSaveHomeState.class);
        vmSaveHomeState.getBanner().observe(getViewLifecycleOwner(), new Observer<ArrayList<Banner>>() {
            @Override
            public void onChanged(ArrayList<Banner> arrayList) {
                UpdateUI(arrayList);
            }
        });

        if(vmSaveHomeState.getBanner().getValue() != null) {
            UpdateUI(vmSaveHomeState.getBanner().getValue());
            Log.e("###","Load banner from viewmodel");
        } else {
            GetDataBanner();
            Log.e("###","Load banner from server");
        }

        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Log.e("bbb","activity create");
    }

    private void ConnectView() {
        //viewPager = view.findViewById(R.id.view_paggerbanner);
        viewPager = view.findViewById(R.id.view_paggerbanner);
        circleIndicator3 = view.findViewById(R.id.indicator_banner);
        progressBar = view.findViewById(R.id.progressbar);

    }

    private void GetDataBanner() {
        progressBar.setVisibility(View.VISIBLE);
        arrBanner = new ArrayList<>();
        RetrofitInterface retrofitInterface = APIUtil.getRetrofitInterface();
        Call<List<Banner>> bannerCallBack = retrofitInterface.GetDataBanner();
        bannerCallBack.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                arrBanner = (ArrayList<Banner>) response.body();
                if(arrBanner != null) {
                    UpdateUI(arrBanner);
                    vmSaveHomeState.getBanner().setValue(arrBanner);
                }
            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                Log.e("###", t.getMessage());
            }
        });
    }
    private void UpdateUI(final ArrayList<Banner> arr) {

        bannerAdapterUpgrade = new BannerAdapterUpgrade(arr);
        viewPager.setAdapter(bannerAdapterUpgrade);
        bannerAdapterUpgrade.setOnItemClickListener(new BannerAdapterUpgrade.OnItemBannerClickListener() {
            @Override
            public void onItemBannerClick(View v, int position) {
                Toast.makeText(getActivity(), arr.get(position).getSongName(), Toast.LENGTH_SHORT).show();

                vmMusicToMiniPlayer = new ViewModelProvider(getActivity()).get(VMMusicToMiniPlayer.class);
//                vmMusicToMiniPlayer.getMusicFromBanner().setValue(arr.get(position));
                //Chuyển đổi từ banner sang song
                ArrayList<Song> tempList = new ArrayList<>();
                for(Banner b : arr) {
                    Song s = new Song();
                    s.setIdSong(b.getIdSong());
                    s.setSongName(b.getSongName());
                    s.setSongSingerName(b.getSongSinger());
                    s.setSongLink(b.getSongLink());
                    s.setSongImage(b.getSongImage());
                    s.setSongLikes("0");
                    tempList.add(s);
                }
                ArrayList<Song> songFromBannerList = new ArrayList<>();
                songFromBannerList.add(tempList.get(position));
                songFromBannerList.addAll(tempList);
                songFromBannerList.remove(position + 1);
                vmMusicToMiniPlayer.getPlayingList().setValue(songFromBannerList);



            }
        });

        viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                int pageWidth = page.getWidth();
                int pageHeight = page.getHeight();
                if(position < -1) {
                    page.setAlpha(0f);
                } else if(position <= 1) {
                    float scaleFactor = Math.max(0.85f, 1 - Math.abs(position));
                    float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                    float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                    if(position < 0) {
                        page.setTranslationX(horzMargin - vertMargin / 2);
                    } else {
                        page.setTranslationX(-horzMargin + vertMargin / 2);
                    }
                    page.setScaleX(scaleFactor);
                    page.setScaleY(scaleFactor);
                    page.setAlpha(0.5f + (scaleFactor - 0.85f) / (1- 0.85f) * (1 - 0.5f));
                } else {
                    page.setAlpha(0f);
                }
            }
        });
        //Số lượng indicator sẽ bằng với số lượng hàm getCount của bannerAdapter
        circleIndicator3.setViewPager(viewPager);
        progressBar.setVisibility(View.GONE);
        //Handler dùng kết hợp cùng runable, thực hiện hành động trong runable với thời gian delay
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                currentItem = viewPager.getCurrentItem();
//                Log.e("banner1", String.valueOf(currentItem));
                currentItem++;
                if(currentItem >= viewPager.getAdapter().getItemCount()) {
                    currentItem = 0;
                }
//                Log.e("banner2", String.valueOf(currentItem));
                viewPager.setCurrentItem(currentItem, true);
                handler.postDelayed(runnable, 4500);
                viewPager.setScrollBarFadeDuration(2);

            }
        };
        handler.postDelayed(runnable, 4500);

    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.e("bbb","resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.e("bbb", "Pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.e("bbb", "Stop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.e("bbb", "destroy view");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.e("bbb","destroy");
    }
}
