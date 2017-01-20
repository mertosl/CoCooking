package com.mertos_l.cocookingfinaldesign;

import android.graphics.Bitmap;

class QuestionReponseFragmentListItem {
    private Bitmap photo;
    private String message;
    private String sujet;
    private String meal;
    private String user;
    private String date;

    QuestionReponseFragmentListItem(Bitmap photo, String message, String sujet, String meal, String date, String user) {
        this.photo = photo;
        this.message = message;
        this.sujet = sujet;
        this.meal = meal;
        this.user = user;
        this.date = date;
    }


    public Bitmap getPhoto() {return photo;}
    public void setPhoto(Bitmap photo) {this.photo = photo;}
    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}
    public String getSujet() {return sujet;}
    public void setSujet(String sujet) {this.sujet = sujet;}
    public String getMeal() {return meal;}
    public void setMeal(String meal) {this.meal = meal;}
    public String getUser() {return user;}
    public void setUser(String user) {this.user = user;}
    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}
}
