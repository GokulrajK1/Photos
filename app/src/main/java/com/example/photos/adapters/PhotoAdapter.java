package com.example.photos.adapters;

import com.bumptech.glide.Glide;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos.R;
import com.example.photos.model.Photo;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.VH> {

    public interface Listener {
        void onClick(int position);
        void onLongClick(int position);
    }

    private final List<Photo> photos;
    private final Listener listener;
    private final Context ctx;

    public PhotoAdapter(Context ctx, List<Photo> photos, Listener l) {
        this.ctx = ctx;
        this.photos = photos;
        this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Photo p = photos.get(pos);

        // Use Glide to load the image in the background
        // Make sure to add: import com.bumptech.glide.Glide; at the top of your file
        try {
            Glide.with(ctx)
                    .load(Uri.parse(p.getUriString()))
                    .error(android.R.drawable.ic_menu_report_image) // Fallback if image fails
                    .centerCrop()
                    .into(h.img);
        } catch (Exception e) {
            h.img.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        // You have a minor warning here: getAdapterPosition() is technically deprecated.
        // If you want to use the modern, warning-free version, change it to getBindingAdapterPosition()
        h.itemView.setOnClickListener(v -> listener.onClick(h.getBindingAdapterPosition()));
        h.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(h.getBindingAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() { return photos.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        VH(View v) {
            super(v);
            img = v.findViewById(R.id.imgThumb);
        }
    }
}