package com.fiktivo.restros;

import android.graphics.Bitmap;

public class Restro implements Comparable<Restro> {
    String id;
    String name;
    String logoURL;
    int offers;
    int distance;
    Bitmap bitmap;

    public Restro(String id, String name, String logoURL, int offers, int distance) {
        this.id = id;
        this.name = name;
        this.logoURL = logoURL;
        this.offers = offers;
        this.distance = distance;
    }

    @Override
    public int compareTo(Restro another) {
        int compareDistance = another.distance;
        return this.distance - compareDistance;
    }
}
