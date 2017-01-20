package com.mertos_l.cocookingfinaldesign;

import android.graphics.Bitmap;

class QuestionRepasFragmentItem {
    private Bitmap photo;
    private String user;
    private String date;
    private String subject;
    private String content;
    private int index;

    QuestionRepasFragmentItem(Bitmap bitmap, String string2, String s, String string3, String string4, int index) {
        this.photo = bitmap;
        this.user = s;
        this.date = string3;
        this.subject = string2;
        this.content = string4;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
