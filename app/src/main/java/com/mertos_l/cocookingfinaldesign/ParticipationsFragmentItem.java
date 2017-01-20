package com.mertos_l.cocookingfinaldesign;

class ParticipationsFragmentItem {
    private String id;
    private String title;
    private String description;
    private String date;
    private String nombre_participants;
    private String prix;

    ParticipationsFragmentItem(String idOrder, String title, String description, String date, String nombre_participants, String prix) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.nombre_participants = nombre_participants;
        this.prix = prix;
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

    public String getNombre_participants() {
        return nombre_participants;
    }

    public void setNombre_participants(String nombre_participants) {
        this.nombre_participants = nombre_participants;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }
}
