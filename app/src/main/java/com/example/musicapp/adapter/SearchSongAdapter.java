package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchSongAdapter extends RecyclerView.Adapter<SearchSongAdapter.ViewHolder> {

    private List<Song> list;
    public ItemClickListener itemClickListener;

    public  SearchSongAdapter(List<Song> list) {
        this.list = list;
    }

    public void SetOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_popularsong, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song item = list.get(position);
        Picasso.get()
                .load(item.getSongImage())
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(holder.ivSongImage);
        holder.tvSongName.setText(item.getSongName());
        holder.tvSinger.setText(item.getSongSingerName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivSongImage;
        private TextView tvSongName, tvSinger;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSongImage = itemView.findViewById(R.id.iv_songicon);
            tvSongName = itemView.findViewById(R.id.tv_songname);
            tvSinger = itemView.findViewById(R.id.tv_songsinger);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClickListener(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
