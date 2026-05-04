package com.example.photos.model;

import java.io.Serializable;

public class Tag implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;   // "person" or "location" only
    private String value;

    public Tag(String name, String value) {
        this.name = name.toLowerCase().trim();
        this.value = value.toLowerCase().trim();
    }

    public String getName() { return name; }
    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag other = (Tag) o;
        return name.equals(other.name) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return (name + value).hashCode();
    }

    @Override
    public String toString() {
        return name + ": " + value;
    }
}