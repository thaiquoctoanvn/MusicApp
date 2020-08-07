package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.PlayList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    public interface recyclerviewItemListener {
        void onItemListener(View view, int position);
    }

    private List<PlayList> list;
    private recyclerviewItemListener itemListener;

    public void setOnItemListener(recyclerviewItemListener itemListener) {
        this.itemListener = itemListener;
    }


    public PlayListAdapter(List<PlayList> list) {
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayList item = list.get(position);
        Picasso.get()
                .load(item.getPlaylistImage())
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(holder.ivPlayListIcon);
        holder.tvPlayListName.setText(item.getPlaylistName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivPlayListIcon;
        TextView tvPlayListName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlayListIcon = (ImageView) itemView.findViewById(R.id.iv_playlisticon);
            tvPlayListName = (TextView) itemView.findViewById(R.id.tv_playlistname);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onItemListener(v, getAdapterPosition());
                }
            });
        }
    }
}
