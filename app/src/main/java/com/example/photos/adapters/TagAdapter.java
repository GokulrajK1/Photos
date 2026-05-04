package com.example.photos.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos.R;
import com.example.photos.model.Tag;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.VH> {

    public interface Listener { void onRemove(int pos); }

    private final List<Tag> tags;
    private final Listener listener;

    public TagAdapter(List<Tag> tags, Listener l) {
        this.tags = tags;
        this.listener = l;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        h.txt.setText(tags.get(pos).toString());
        h.btn.setOnClickListener(v -> listener.onRemove(h.getAdapterPosition()));
    }

    @Override
    public int getItemCount() { return tags.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView txt;
        Button btn;
        VH(View v) {
            super(v);
            txt = v.findViewById(R.id.txtTag);
            btn = v.findViewById(R.id.btnRemoveTag);
        }
    }
}