package com.androiddevs.firebasestoragerecyclerviewjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    List<ImageData> images;

    public ImageAdapter(List<ImageData> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageViewHolder holder, int position) {
        ImageData imageData = images.get(position);
        holder.tvTitle.setText(imageData.getTitle());
        holder.tvDescription.setText(imageData.getDescription());

        Glide.with(holder.itemView)
                .load(imageData.getUrl())
                .into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImage;
        TextView tvTitle, tvDescription;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
