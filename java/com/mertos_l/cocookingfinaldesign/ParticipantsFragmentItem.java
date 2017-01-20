package com.mertos_l.cocookingfinaldesign;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

class ParticipantsFragmentItem {
    private String id;
    private Bitmap photo;
    private String user;
    private String prix;

    ParticipantsFragmentItem(String id, Bitmap photo, String user, String prix) {
        this.id = id;
        this.photo = photo;
        this.user = user;
        this.prix = prix;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }
}
