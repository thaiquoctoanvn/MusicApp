package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.ItemLibrary;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemLibraryAdapter extends RecyclerView.Adapter<ItemLibraryAdapter.ViewHolder> {

    private ItemClickListener itemClickListener;
    private List<ItemLibrary> list;

    public ItemLibraryAdapter(List<ItemLibrary> list) {
        this.list = list;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemLibrary item = list.get(position);
        holder.ivItemIcon.setImageResource(item.getSrcItemIcon());
        holder.tvItemName.setText(item.getItemName());
        holder.tvItemDetail.setText(item.getItemDetail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivItemIcon;
        private TextView tvItemName, tvItemDetail;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemIcon = itemView.findViewById(R.id.iv_itemicon);
            tvItemName = itemView.findViewById(R.id.tv_itemname);
            tvItemDetail = itemView.findViewById(R.id.tv_itemdetail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClickListener(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
