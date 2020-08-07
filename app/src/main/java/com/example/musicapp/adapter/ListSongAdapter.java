package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.ViewHolder> {

    public interface OnItemSongListener {
        void OnItemSongClick(View v, int position);
    }

    private OnItemSongListener onItemSongListener;
    private List<Song> list;
    private int STT = 0;

    public void setOnItemSongClickListener(OnItemSongListener callBack) {
        this.onItemSongListener = callBack;
    }

    public ListSongAdapter(List<Song> list) {
        this.list = list;
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
                .into(holder.ivListSongItemImage);
        STT++;
        holder.tvListSongItemName.setText(String.valueOf(STT) + ". " + item.getSongName());
        holder.tvListSongItemSinger.setText(item.getSongSingerName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivListSongItemImage;
        private TextView tvListSongItemName, tvListSongItemSinger;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivListSongItemImage = itemView.findViewById(R.id.iv_listsongitemimage);
            tvListSongItemName = itemView.findViewById(R.id.tv_listsongitemname);
            tvListSongItemSinger = itemView.findViewById(R.id.tv_listsongitemsinger);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSongListener.OnItemSongClick(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
