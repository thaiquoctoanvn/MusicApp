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
import com.example.musicapp.object.PopularSong;
import com.example.musicapp.object.Song;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

public class PopularSongAdapter extends RecyclerView.Adapter<PopularSongAdapter.ViewHolder> {

    public interface OnItemPopularSongListener {
        void onItemPopularSongClick(View v, int position);
    }

    public interface AddSongToLocalListListener {
        void addToLocalListClick(View v, int position);
    }

    private OnItemPopularSongListener onItemPopularSongListener;
    private AddSongToLocalListListener addSongToLocalListListener;
    private List<Song> list;

    public void setOnItemPopularSongClickListener(OnItemPopularSongListener listener) {
        this.onItemPopularSongListener = listener;
    }

    public void AddToLocalListClickListener(AddSongToLocalListListener addSongToLocalListListener) {
        this.addSongToLocalListListener = addSongToLocalListListener;
    }
    public PopularSongAdapter(List<Song> list) {
        this.list = list;
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
                .into(holder.ivPopularSongIcon);
        holder.tvPopularSongName.setText(item.getSongName());
        holder.tvPopularSongSinger.setText(item.getSongSingerName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivPopularSongIcon;
        private ImageButton ibtnAddSongTo;
        private TextView tvPopularSongName;
        private TextView tvPopularSongSinger;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPopularSongIcon = itemView.findViewById(R.id.iv_songicon);
            tvPopularSongName = itemView.findViewById(R.id.tv_songname);
            tvPopularSongSinger = itemView.findViewById(R.id.tv_songsinger);
            ibtnAddSongTo = itemView.findViewById(R.id.ibtn_addsongto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemPopularSongListener.onItemPopularSongClick(v, getAbsoluteAdapterPosition());
                }
            });

            ibtnAddSongTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSongToLocalListListener.addToLocalListClick(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
