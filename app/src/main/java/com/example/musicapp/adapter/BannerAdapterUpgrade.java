package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.Banner;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerAdapterUpgrade extends RecyclerView.Adapter<BannerAdapterUpgrade.ViewHolder> {

    public interface OnItemBannerClickListener {
        void onItemBannerClick(View v, int position);
    }

    private List<Banner> list;
    private OnItemBannerClickListener onItemBannerClickListener;

    public void setOnItemClickListener(OnItemBannerClickListener onItemClickListener) {
        this.onItemBannerClickListener = onItemClickListener;
    }

    public BannerAdapterUpgrade(List<Banner> list) {
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Banner item = list.get(position);
        Picasso.get()
                .load(item.getBannerImage())
                .placeholder(R.color.white)
                .error(R.color.white)
                .into(holder.ivBackGround);
        Picasso.get()
                .load(item.getSongImage())
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(holder.ivIcon);
        holder.tvName.setText(item.getSongName());
        holder.tvContent.setText(item.getBannerDescription());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivBackGround, ivIcon;
        private TextView tvName, tvContent;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBackGround = itemView.findViewById(R.id.iv_backgroundbanner);
            ivIcon = itemView.findViewById(R.id.iv_iconbanner);
            tvName = itemView.findViewById(R.id.tv_bannertitle);
            tvContent = itemView.findViewById(R.id.tv_bannercontent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemBannerClickListener.onItemBannerClick(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
