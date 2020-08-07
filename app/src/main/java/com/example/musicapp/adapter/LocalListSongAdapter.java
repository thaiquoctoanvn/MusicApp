package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.SongLocal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LocalListSongAdapter extends RecyclerView.Adapter<LocalListSongAdapter.ViewHolder> {

    private List<SongLocal> list;
    private ItemClickListener itemClickListener;

    public LocalListSongAdapter(List<SongLocal> list) {
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
        SongLocal item = list.get(position);
        Picasso.get()
                .load(item.getSongImage())
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(holder.ivLocalSongImage);
        holder.tvLocalSongName.setText(item.getSongName());
        holder.tvLocalListSongSinger.setText(item.getSinger());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivLocalSongImage;
        private TextView tvLocalSongName, tvLocalListSongSinger;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLocalSongImage = itemView.findViewById(R.id.iv_listsongitemimage);
            tvLocalSongName = itemView.findViewById(R.id.tv_listsongitemname);
            tvLocalListSongSinger = itemView.findViewById(R.id.tv_listsongitemsinger);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClickListener(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
