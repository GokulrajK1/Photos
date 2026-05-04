package com.example.photos.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos.R;
import com.example.photos.adapters.PhotoAdapter;
import com.example.photos.model.Album;
import com.example.photos.model.DataManager;
import com.example.photos.model.Photo;

public class AlbumActivity extends AppCompatActivity {

    private Album album;
    private PhotoAdapter adapter;

    private final ActivityResultLauncher<String[]> pickImage = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) return;
                try {
                    getContentResolver().takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {}
                Photo p = new Photo(uri.toString());
                if (!album.addPhoto(p)) {
                    Toast.makeText(this, "Photo already in album", Toast.LENGTH_SHORT).show();
                    return;
                }
                DataManager.save(this);
                adapter.notifyItemInserted(album.size() - 1);
            });

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_album);

        String name = getIntent().getStringExtra("albumName");
        album = DataManager.findAlbum(this, name);
        if (album == null) { finish(); return; }

        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle(album.getName());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv = findViewById(R.id.recyclerPhotos);
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PhotoAdapter(this, album.getPhotos(), new PhotoAdapter.Listener() {
            @Override
            public void onClick(int pos) {
                Intent i = new Intent(AlbumActivity.this, PhotoActivity.class);
                i.putExtra("albumName", album.getName());
                i.putExtra("photoIndex", pos);
                startActivity(i);
            }
            @Override
            public void onLongClick(int pos) {
                showPhotoOptions(pos);
            }
        });
        rv.setAdapter(adapter);

        ((Button) findViewById(R.id.btnAddPhoto)).setOnClickListener(v ->
                pickImage.launch(new String[]{"image/*"}));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void showPhotoOptions(int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Photo Options")
                .setItems(new String[]{"Remove from album"}, (d, w) -> {
                    album.getPhotos().remove(pos);
                    DataManager.save(this);
                    adapter.notifyItemRemoved(pos);
                    Toast.makeText(this, "Photo removed", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
