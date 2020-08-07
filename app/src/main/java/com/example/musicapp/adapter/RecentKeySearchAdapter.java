package com.example.musicapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.RecentKeySearch;

import java.util.List;

public class RecentKeySearchAdapter extends RecyclerView.Adapter<RecentKeySearchAdapter.ViewHolder> {

    private List<RecentKeySearch> list;

    public RecentKeySearchAdapter(List<RecentKeySearch> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recentkyesearch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentKeySearch item = list.get(position);
        holder.tvKeySearch.setText(item.getKeys());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvKeySearch;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKeySearch = itemView.findViewById(R.id.tv_recentkeysearch);
        }
    }
}
