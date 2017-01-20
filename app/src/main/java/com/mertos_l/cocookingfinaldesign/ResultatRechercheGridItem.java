package com.mertos_l.cocookingfinaldesign;

import android.graphics.Bitmap;

class ResultatRechercheGridItem {
    Bitmap photo;
    String title;
    String places_free;
    String price;
    String date;

    ResultatRechercheGridItem(Bitmap photo, String title, String places_free, String price, String date) {
        this.photo = photo;
        this.title = title;
        this.places_free = places_free;
        this.price = price;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaces_free() {
        return places_free;
    }

    public void setPlaces_free(String places_free) {
        this.places_free = places_free;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
