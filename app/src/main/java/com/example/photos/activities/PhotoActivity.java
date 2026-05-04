package com.example.photos.activities;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos.R;
import com.example.photos.adapters.TagAdapter;
import com.example.photos.model.Album;
import com.example.photos.model.DataManager;
import com.example.photos.model.Photo;
import com.example.photos.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    private Album album;
    private int index;
    private ImageView img;
    private TextView txtIndex;
    private RecyclerView recyclerTags;
    private TagAdapter tagAdapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_photo);

        String name = getIntent().getStringExtra("albumName");
        index = getIntent().getIntExtra("photoIndex", 0);
        album = DataManager.findAlbum(this, name);
        if (album == null || album.size() == 0) { finish(); return; }

        img = findViewById(R.id.imgPhoto);
        txtIndex = findViewById(R.id.txtIndex);
        recyclerTags = findViewById(R.id.recyclerTags);
        recyclerTags.setLayoutManager(new LinearLayoutManager(this));

        ((Button) findViewById(R.id.btnPrev)).setOnClickListener(v -> {
            if (index > 0) { index--; render(); }
        });
        ((Button) findViewById(R.id.btnNext)).setOnClickListener(v -> {
            if (index < album.size() - 1) { index++; render(); }
        });
        ((Button) findViewById(R.id.btnAddTag)).setOnClickListener(v -> showAddTag());
        ((Button) findViewById(R.id.btnMove)).setOnClickListener(v -> showMove());

        render();
    }

    private void render() {
        Photo p = album.getPhotos().get(index);
        try {
            img.setImageURI(Uri.parse(p.getUriString()));
        } catch (Exception e) {
            img.setImageResource(android.R.drawable.ic_menu_report_image);
        }
        txtIndex.setText((index + 1) + " / " + album.size());

        tagAdapter = new TagAdapter(p.getTags(), pos -> {
            p.getTags().remove(pos);
            DataManager.save(this);
            tagAdapter.notifyItemRemoved(pos);
        });
        recyclerTags.setAdapter(tagAdapter);
    }

    private void showAddTag() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> sa = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"person", "location"});
        spinner.setAdapter(sa);

        EditText input = new EditText(this);
        input.setHint("Tag value");

        layout.addView(spinner);
        layout.addView(input);

        new AlertDialog.Builder(this)
                .setTitle("Add Tag")
                .setView(layout)
                .setPositiveButton("Add", (d, w) -> {
                    String value = input.getText().toString().trim();
                    if (value.isEmpty()) {
                        Toast.makeText(this, "Value required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Tag t = new Tag((String) spinner.getSelectedItem(), value);
                    Photo p = album.getPhotos().get(index);
                    if (!p.addTag(t)) {
                        Toast.makeText(this, "Tag already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DataManager.save(this);
                    tagAdapter.notifyItemInserted(p.getTags().size() - 1);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMove() {
        List<Album> all = DataManager.getAlbums(this);
        List<Album> targets = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (Album a : all) {
            if (a != album) {
                targets.add(a);
                names.add(a.getName());
            }
        }
        if (targets.isEmpty()) {
            Toast.makeText(this, "No other albums available", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Move to album")
                .setItems(names.toArray(new String[0]), (d, which) -> {
                    Photo p = album.getPhotos().get(index);
                    Album target = targets.get(which);
                    if (!target.addPhoto(p)) {
                        Toast.makeText(this, "Photo already in target album", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    album.getPhotos().remove(index);
                    DataManager.save(this);
                    if (album.size() == 0) { finish(); return; }
                    if (index >= album.size()) index = album.size() - 1;
                    render();
                })
                .show();
    }
}