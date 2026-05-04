package com.example.photos.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos.R;
import com.example.photos.adapters.AlbumAdapter;
import com.example.photos.model.Album;
import com.example.photos.model.DataManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Album> albums;
    private AlbumAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        albums = DataManager.getAlbums(this);

        RecyclerView rv = findViewById(R.id.recyclerAlbums);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlbumAdapter(albums, new AlbumAdapter.Listener() {
            @Override
            public void onClick(Album a) {
                Intent i = new Intent(MainActivity.this, AlbumActivity.class);
                i.putExtra("albumName", a.getName());
                startActivity(i);
            }
            @Override
            public void onLongClick(Album a, int pos) {
                showAlbumOptions(a, pos);
            }
        });
        rv.setAdapter(adapter);

        ((Button) findViewById(R.id.btnNewAlbum)).setOnClickListener(v -> showCreateDialog());
        ((Button) findViewById(R.id.btnSearch)).setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void showCreateDialog() {
        EditText input = new EditText(this);
        input.setHint("Album name");
        new AlertDialog.Builder(this)
                .setTitle("New Album")
                .setView(input)
                .setPositiveButton("Create", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (DataManager.findAlbum(this, name) != null) {
                        Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    albums.add(new Album(name));
                    DataManager.save(this);
                    adapter.notifyItemInserted(albums.size() - 1);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAlbumOptions(Album a, int pos) {
        new AlertDialog.Builder(this)
                .setTitle(a.getName())
                .setItems(new String[]{"Rename", "Delete"}, (d, which) -> {
                    if (which == 0) showRenameDialog(a, pos);
                    else showDeleteDialog(a, pos);
                })
                .show();
    }

    private void showRenameDialog(Album a, int pos) {
        EditText input = new EditText(this);
        input.setText(a.getName());
        new AlertDialog.Builder(this)
                .setTitle("Rename Album")
                .setView(input)
                .setPositiveButton("Save", (d, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) return;
                    Album existing = DataManager.findAlbum(this, name);
                    if (existing != null && existing != a) {
                        Toast.makeText(this, "Name already used", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    a.setName(name);
                    DataManager.save(this);
                    adapter.notifyItemChanged(pos);
                    Toast.makeText(this, "Album renamed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteDialog(Album a, int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Delete \"" + a.getName() + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    albums.remove(pos);
                    DataManager.save(this);
                    adapter.notifyItemRemoved(pos);
                    Toast.makeText(this, "Album deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}