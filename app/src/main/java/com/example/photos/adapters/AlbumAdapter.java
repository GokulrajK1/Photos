package com.example.photos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos.R;
import com.example.photos.model.Album;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.VH> {

    public interface Listener {
        void onClick(Album a);
        void onLongClick(Album a, int position);
    }

    private final List<Album> albums;
    private final Listener listener;

    public AlbumAdapter(List<Album> albums, Listener l) {
        this.albums = albums;
        this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Album a = albums.get(pos);
        h.name.setText(a.getName());
        h.count.setText(a.size() + " photo" + (a.size() == 1 ? "" : "s"));
        h.itemView.setOnClickListener(v -> listener.onClick(a));
        h.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(a, h.getBindingAdapterPosition());
            return true;
        });
        h.menu.setOnClickListener(v -> listener.onLongClick(a, h.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() { return albums.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, count;
        ImageView menu;
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.txtAlbumName);
            count = v.findViewById(R.id.txtAlbumCount);
            menu = v.findViewById(R.id.btnAlbumMenu);
        }
    }
}