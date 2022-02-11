package com.example.localim;

public class User {
    private String nom;
    private String prenom;
    private String email;
    private String numTel;
    private int credit;


    public User(){
    }

    public User(String nom, String prenom, String email, String numTel, int credit) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numTel = numTel;
        this.credit = credit;
    }
    public User( int credit,String prenom, String email, String numTel,String nom) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numTel = numTel;
        this.credit = credit;
    }
    public User(String nom, String prenom, String email, String numTel) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.numTel = numTel;
        this.credit = 10;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }



    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
