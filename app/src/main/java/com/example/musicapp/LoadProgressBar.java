package com.example.musicapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

public class LoadProgressBar {

    public static ProgressBar progressBar;
    public LoadProgressBar(Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.progress_bar, null);
        progressBar = view.findViewById(R.id.bar);
    }
    public void showProgressBar() {

        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
