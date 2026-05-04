package com.example.photos.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photos.R;
import com.example.photos.adapters.PhotoAdapter;
import com.example.photos.model.Album;
import com.example.photos.model.DataManager;
import com.example.photos.model.Photo;
import com.example.photos.model.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private Spinner spType1, spType2, spOp;
    private AutoCompleteTextView autoVal1, autoVal2;
    private RecyclerView recycler;


    private TextView txtCount;
    private final List<Photo> results = new ArrayList<>();
    private PhotoAdapter adapter;

    private static final String[] TAG_TYPES = {"person", "location"};
    private static final String[] OPS = {"single", "AND", "OR"};

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_search);

        spType1 = findViewById(R.id.spinnerType1);
        spType2 = findViewById(R.id.spinnerType2);
        spOp = findViewById(R.id.spinnerOp);
        autoVal1 = findViewById(R.id.autoValue1);
        autoVal2 = findViewById(R.id.autoValue2);
        recycler = findViewById(R.id.recyclerResults);
        txtCount = findViewById(R.id.txtCount);

        spType1.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, TAG_TYPES));
        spType2.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, TAG_TYPES));
        spOp.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, OPS));

        setupAutoComplete(spType1, autoVal1);
        setupAutoComplete(spType2, autoVal2);

        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PhotoAdapter(this, results, new PhotoAdapter.Listener() {
            @Override public void onClick(int pos) { /* preview only */ }
            @Override public void onLongClick(int pos) { }
        });
        recycler.setAdapter(adapter);

        ((Button) findViewById(R.id.btnRunSearch)).setOnClickListener(v -> runSearch());
    }

    private void setupAutoComplete(Spinner typeSpinner, AutoCompleteTextView field) {
        field.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {
                String type = (String) typeSpinner.getSelectedItem();
                String prefix = s.toString().toLowerCase().trim();
                List<String> matches = new ArrayList<>();
                Set<String> seen = new HashSet<>();
                for (Album al : DataManager.getAlbums(SearchActivity.this)) {
                    for (Photo p : al.getPhotos()) {
                        for (Tag t : p.getTags()) {
                            if (t.getName().equals(type) && t.getValue().startsWith(prefix)
                                    && seen.add(t.getValue())) {
                                matches.add(t.getValue());
                            }
                        }
                    }
                }
                field.setAdapter(new ArrayAdapter<>(SearchActivity.this,
                        android.R.layout.simple_dropdown_item_1line, matches));
            }
        });
    }

    private void runSearch() {
        String type1 = (String) spType1.getSelectedItem();
        String val1 = autoVal1.getText().toString().toLowerCase().trim();
        String op = (String) spOp.getSelectedItem();
        String type2 = (String) spType2.getSelectedItem();
        String val2 = autoVal2.getText().toString().toLowerCase().trim();

        results.clear();
        Set<Photo> seen = new HashSet<>();

        for (Album al : DataManager.getAlbums(this)) {
            for (Photo p : al.getPhotos()) {
                if (seen.contains(p)) continue;
                boolean match1 = !val1.isEmpty() && hasMatchingTag(p, type1, val1);
                boolean match2 = !val2.isEmpty() && hasMatchingTag(p, type2, val2);

                boolean keep;
                if (op.equals("AND")) keep = match1 && match2;
                else if (op.equals("OR")) keep = match1 || match2;
                else keep = match1; // single

                if (keep) {
                    results.add(p);
                    seen.add(p);
                }
            }
        }
        txtCount.setText(results.size() + " result" + (results.size() == 1 ? "" : "s"));
        adapter.notifyDataSetChanged();
    }

    private boolean hasMatchingTag(Photo p, String type, String value) {
        for (Tag t : p.getTags()) {
            if (t.getName().equals(type) && t.getValue().equals(value)) return true;
        }
        return false;
    }
}