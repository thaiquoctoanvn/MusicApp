package com.example.musicapp.adapter;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.DownloadedSong;
import com.example.musicapp.object.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DownloadedSongAdapter extends RecyclerView.Adapter<DownloadedSongAdapter.ViewHolder> {

    private List<Song> list;
    private ItemClickListener itemClickListener;

    public DownloadedSongAdapter(List<Song> list) {
        this.list = list;
    }

    public void SetOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_listsong, parent, false);
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
        holder.tvSongSinger.setText(item.getSongSingerName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivSongImage;
        private TextView tvSongName, tvSongSinger;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSongImage = (ImageView) itemView.findViewById(R.id.iv_listsongitemimage);
            tvSongName = (TextView) itemView.findViewById(R.id.tv_listsongitemname);
            tvSongSinger = (TextView) itemView.findViewById(R.id.tv_listsongitemsinger);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClickListener(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
