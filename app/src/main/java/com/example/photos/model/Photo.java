package com.example.photos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uriString;
    private List<Tag> tags;

    public Photo(String uriString) {
        this.uriString = uriString;
        this.tags = new ArrayList<>();
    }

    public String getUriString() { return uriString; }
    public List<Tag> getTags() { return tags; }

    public boolean addTag(Tag t) {
        if (tags.contains(t)) return false;
        tags.add(t);
        return true;
    }

    public boolean removeTag(Tag t) {
        return tags.remove(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;
        return uriString.equals(((Photo) o).uriString);
    }

    @Override
    public int hashCode() {
        return uriString.hashCode();
    }
}