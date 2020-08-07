package com.example.musicapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.object.Type;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    public interface TypeItemListener {
        void onItemClick(View v, int position);
    }

    private TypeItemListener typeItemListener;
    private List<Type> listType;

    public void setOnItemListener(TypeItemListener typeItemListener) {
        this.typeItemListener = typeItemListener;
    }

    public TypeAdapter(List<Type> listType) {
        this.listType = listType;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Type item = listType.get(position);
        Picasso.get()
                .load(item.getTypeImage())
                .placeholder(R.mipmap.default_image)
                .error(R.mipmap.default_image)
                .into(holder.ivTypeIcon);
        holder.tvTypeName.setText(item.getTypeName());
    }

    @Override
    public int getItemCount() {
        return listType.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivTypeIcon;
        private TextView tvTypeName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTypeIcon = (ImageView) itemView.findViewById(R.id.iv_typeicon);
            tvTypeName = (TextView) itemView.findViewById(R.id.tv_typename);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeItemListener.onItemClick(v, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
