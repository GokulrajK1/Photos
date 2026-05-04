package com.example.photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Photo> photos;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Photo> getPhotos() { return photos; }

    public boolean addPhoto(Photo p) {
        if (photos.contains(p)) return false;
        photos.add(p);
        return true;
    }

    public boolean removePhoto(Photo p) {
        return photos.remove(p);
    }

    public int size() { return photos.size(); }

    @Override
    public String toString() { return name; }
}