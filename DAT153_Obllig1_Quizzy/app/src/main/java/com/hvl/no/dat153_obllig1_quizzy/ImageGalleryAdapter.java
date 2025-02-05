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

    private Context context;
    private List<GalleryItem> galleryItems;

    public ImageGalleryAdapter(Context context, List<GalleryItem> galleryItems) {
        this.context = context;
        this.galleryItems = galleryItems;
    }
    // REVIEW THIS CODE1!!! prøv å forstå alt
    @NonNull
    @Override
    public ImageGalleryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);
        return new ImageGalleryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGalleryAdapter.MyViewHolder holder, int position) {
        GalleryItem item = galleryItems.get(position);

        holder.imageTextView.setText(item.getName());

        Glide.with(context)
                .load(item.getImageUri()) // Load from URI
                .placeholder(R.drawable.placeholder) // Show placeholder while loading
                .error(R.drawable.error_image) // Show error image if failed
                .into(holder.imageView);

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
