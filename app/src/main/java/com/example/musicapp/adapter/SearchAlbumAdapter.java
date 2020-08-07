package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.Album;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAlbumAdapter extends RecyclerView.Adapter<SearchAlbumAdapter.ViewHolder> {

    private List<Album> list;
    public ItemClickListener itemClickListener;

    public SearchAlbumAdapter(List<Album> list) {
        this.list = list;
    }

    public void SetOnItemClickListener( ItemClickListener itemClickListener) {
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
        Album item = list.get(position);
        Picasso.get()
                .load(item.getAlbumImage())
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(holder.ivAlbumImage);
        holder.tvAlbumName.setText(item.getAlbumName());
        holder.tvAlbumSinger.setText(item.getSingerName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivAlbumImage;
        private TextView tvAlbumName, tvAlbumSinger;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbumImage = itemView.findViewById(R.id.iv_songicon);
            tvAlbumName = itemView.findViewById(R.id.tv_songname);
            tvAlbumSinger = itemView.findViewById(R.id.tv_songsinger);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClickListener(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
