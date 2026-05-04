package com.example.photos.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos.R;
import com.example.photos.model.Photo;

import java.io.InputStream;
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
        
        try (InputStream is = ctx.getContentResolver().openInputStream(Uri.parse(p.getUriString()))) {
            Bitmap bm = BitmapFactory.decodeStream(is);
            h.img.setImageBitmap(bm);
        } catch (Exception e) {
            h.img.setImageResource(android.R.drawable.ic_menu_report_image);
        }

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
