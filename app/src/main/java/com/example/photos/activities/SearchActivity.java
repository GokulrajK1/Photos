package com.example.photos.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    private final List<String> resultAlbumNames = new ArrayList<>();
    private final List<Integer> resultIndices = new ArrayList<>();
    private PhotoAdapter adapter;

    private static final String[] TAG_TYPES = {"person", "location"};
    private static final String[] OPS = {"single", "AND", "OR"};

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_search);
        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Search Photos");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spType1 = findViewById(R.id.spinnerType1);
        spType2 = findViewById(R.id.spinnerType2);
        spOp = findViewById(R.id.spinnerOp);
        autoVal1 = findViewById(R.id.autoValue1);
        autoVal2 = findViewById(R.id.autoValue2);
        recycler = findViewById(R.id.recyclerResults);
        txtCount = findViewById(R.id.txtCount);

        spType1.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TAG_TYPES));
        spType2.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, TAG_TYPES));
        spOp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, OPS));

        autoVal1.setThreshold(1);
        autoVal2.setThreshold(1);

        autoVal1.setOnItemClickListener((parent, view, position, id) -> runSearch());
        autoVal2.setOnItemClickListener((parent, view, position, id) -> runSearch());

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int count) { runSearch(); }
            @Override public void afterTextChanged(Editable s) {}
        };
        autoVal1.addTextChangedListener(watcher);
        autoVal2.addTextChangedListener(watcher);

        AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                updateAllSuggestions();
                runSearch();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        };
        spType1.setOnItemSelectedListener(spinnerListener);
        spType2.setOnItemSelectedListener(spinnerListener);
        spOp.setOnItemSelectedListener(spinnerListener);

        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new PhotoAdapter(this, results, new PhotoAdapter.Listener() {
            @Override 
            public void onClick(int pos) { 
                Intent i = new Intent(SearchActivity.this, PhotoActivity.class);
                i.putExtra("albumName", resultAlbumNames.get(pos));
                i.putExtra("photoIndex", resultIndices.get(pos));
                startActivity(i);
            }
            @Override public void onLongClick(int pos) { }
        });
        recycler.setAdapter(adapter);

        ((Button) findViewById(R.id.btnRunSearch)).setOnClickListener(v -> runSearch());
        
        updateAllSuggestions();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void updateAllSuggestions() {
        updateFieldSuggestions(spType1, autoVal1);
        updateFieldSuggestions(spType2, autoVal2);
    }

    private void updateFieldSuggestions(Spinner spinner, AutoCompleteTextView field) {
        String type = (String) spinner.getSelectedItem();
        final List<String> allValues = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Album al : DataManager.getAlbums(this)) {
            for (Photo p : al.getPhotos()) {
                for (Tag t : p.getTags()) {
                    if (t.getName().equalsIgnoreCase(type)) {
                        String val = t.getValue();
                        if (seen.add(val.toLowerCase())) {
                            allValues.add(val);
                        }
                    }
                }
            }
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(allValues)) {
            @NonNull
            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults fr = new FilterResults();
                        if (constraint == null || constraint.length() == 0) {
                            fr.values = allValues;
                            fr.count = allValues.size();
                        } else {
                            String prefix = constraint.toString().toLowerCase();
                            List<String> list = new ArrayList<>();
                            for (String s : allValues) {
                                if (s.toLowerCase().startsWith(prefix)) list.add(s);
                            }
                            fr.values = list;
                            fr.count = list.size();
                        }
                        return fr;
                    }
                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults fr) {
                        clear();
                        if (fr != null && fr.values != null) {
                            addAll((List<String>) fr.values);
                        }
                        notifyDataSetChanged();
                    }
                };
            }
        };
        field.setAdapter(adapter);
    }

    private void runSearch() {
        String type1 = (String) spType1.getSelectedItem();
        String val1 = autoVal1.getText().toString().toLowerCase().trim();
        String op = (String) spOp.getSelectedItem();
        String type2 = (String) spType2.getSelectedItem();
        String val2 = autoVal2.getText().toString().toLowerCase().trim();

        results.clear();
        resultAlbumNames.clear();
        resultIndices.clear();

        if (val1.isEmpty() && val2.isEmpty()) {
            txtCount.setText("0 results");
            adapter.notifyDataSetChanged();
            return;
        }

        for (Album al : DataManager.getAlbums(this)) {
            List<Photo> photos = al.getPhotos();
            for (int i = 0; i < photos.size(); i++) {
                Photo p = photos.get(i);
                boolean match1 = !val1.isEmpty() && hasMatchingTag(p, type1, val1);
                boolean match2 = !val2.isEmpty() && hasMatchingTag(p, type2, val2);

                boolean keep;
                if (op.equals("AND")) keep = match1 && match2;
                else if (op.equals("OR")) keep = match1 || match2;
                else keep = match1;

                if (keep) {
                    results.add(p);
                    resultAlbumNames.add(al.getName());
                    resultIndices.add(i);
                }
            }
        }
        txtCount.setText(results.size() + " result" + (results.size() == 1 ? "" : "s"));
        adapter.notifyDataSetChanged();
    }

    private boolean hasMatchingTag(Photo p, String type, String value) {
        for (Tag t : p.getTags()) {
            if (t.getName().equalsIgnoreCase(type) && t.getValue().toLowerCase().startsWith(value)) return true;
        }
        return false;
    }
}
