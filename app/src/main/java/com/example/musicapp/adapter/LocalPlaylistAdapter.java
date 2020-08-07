package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.PlayListLocal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LocalPlaylistAdapter extends RecyclerView.Adapter<LocalPlaylistAdapter.ViewHolder> {

    private ItemClickListener itemClickListener;
    private List<PlayListLocal> list;

    public LocalPlaylistAdapter(List<PlayListLocal> list) {
        this.list = list;
    }

    public void SetOnLocalListItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_localplaylist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayListLocal item = list.get(position);
        Picasso.get()
                .load(item.getPlaylistImage())
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(holder.ivLocalPlaylistImage);
        holder.tvLocalPlaylistName.setText(item.getPlaylistName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivLocalPlaylistImage;
        private TextView tvLocalPlaylistName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLocalPlaylistImage = itemView.findViewById(R.id.iv_localplaylistimage);
            tvLocalPlaylistName = itemView.findViewById(R.id.tv_localplaylistname);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClickListener(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
