package com.mertos_l.cocookingfinaldesign;

class HistoriqueRepasFragmentItem {
    private String id;
    private String title;
    private String description;
    private String date;
    private String price;

    HistoriqueRepasFragmentItem(String id, String title, String description, String date, String price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}