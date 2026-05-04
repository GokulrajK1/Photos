package com.example.photos.model;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String FILE_NAME = "photos_data.dat";
    private static List<Album> albums;

    public static List<Album> getAlbums(Context ctx) {
        if (albums == null) {
            albums = load(ctx);
        }
        return albums;
    }

    @SuppressWarnings("unchecked")
    private static List<Album> load(Context ctx) {
        File f = new File(ctx.getFilesDir(), FILE_NAME);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Album>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void save(Context ctx) {
        if (albums == null) return;
        File f = new File(ctx.getFilesDir(), FILE_NAME);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(albums);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Album findAlbum(Context ctx, String name) {
        for (Album a : getAlbums(ctx)) {
            if (a.getName().equalsIgnoreCase(name)) return a;
        }
        return null;
    }
}