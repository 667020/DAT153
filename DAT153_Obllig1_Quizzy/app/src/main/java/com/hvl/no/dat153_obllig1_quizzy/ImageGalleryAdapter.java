package com.hvl.no.dat153_obllig1_quizzy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.hvl.no.dat153_obllig1_quizzy.features.gallery.model.GalleryItem;

import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder> {

    public interface OnGalleryItemClickListener {
        void onGalleryItemClicked(GalleryItem item, int position);
    }

    private Context context;
    private List<GalleryItem> galleryItems;
    private OnGalleryItemClickListener listener;

    public ImageGalleryAdapter(Context context, List<GalleryItem> galleryItems, OnGalleryItemClickListener listener) {
        this.context = context;
        this.galleryItems = galleryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GalleryItem item = galleryItems.get(position);
        holder.imageTextView.setText(item.getName());
        // load image with Glide or similar
        Glide.with(context)
                .load(item.getImageUri())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(holder.imageView);

        // Set click listener on the entire item.
        holder.itemView.setOnClickListener(v -> {
            if(listener != null) {
                listener.onGalleryItemClicked(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView imageTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageTextView= itemView.findViewById(R.id.textImageName);
        }
    }
}
