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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    public interface OnItemAlbumListener {
        void onItemAlbumClick(View v, int position);
    }

    private OnItemAlbumListener onItemAlbumListener;
    private List<Album> list;

    public void setOnItemAlbumListener(OnItemAlbumListener onItemAlbumListener) {
        this.onItemAlbumListener = onItemAlbumListener;
    }

    public AlbumAdapter(List<Album> list) {
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album item = list.get(position);
        Picasso.get()
                .load(item.getAlbumImage())
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(holder.ivAlbumIcon);
        holder.tvAlbumName.setText(item.getAlbumName());
        holder.tvSingerName.setText(item.getSingerName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivAlbumIcon;
        private TextView tvAlbumName;
        private TextView tvSingerName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbumIcon = itemView.findViewById(R.id.iv_albumicon);
            tvAlbumName = itemView.findViewById(R.id.tv_albumname);
            tvSingerName = itemView.findViewById(R.id.tv_singername);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemAlbumListener.onItemAlbumClick(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
