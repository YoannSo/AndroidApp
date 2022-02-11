package com.example.localim;

import android.graphics.Bitmap;

public class Services {
    private String titre;
    private String texte;
    private Bitmap image;
   private  String lieu;
   private String userKey;
   private int cost;

   private String inDatabaseUrl=null;
   private String keyInDatabase;



    public Services(String titre, String texte, String lieu,String url,String userKey,String key,int cost) {
        this.titre = titre;
        this.texte = texte;
        this.inDatabaseUrl = url;
        this.lieu = lieu;
        this.userKey=userKey;
        this.keyInDatabase=key;
        this.cost=cost;
    }
    public Services(String titre, String texte, String lieu,String url,String id,int cost) {
        this.titre = titre;
        this.texte = texte;
        this.inDatabaseUrl = url;
        this.lieu = lieu;
        this.userKey=id;
        this.cost=cost;
    }
    public Services(String titre, String texte, String lieu) {
        this.titre = titre;
        this.texte = texte;
        this.lieu = lieu;
    }
   /* public Services(String titre, String texte, String lieu) {
        this.titre = titre;
        this.texte = texte;
        this.lieu = lieu;
    }*/
    public Services(){}
    public String getTitre() {
        return titre;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getId() {
        return userKey;
    }

    public void setId(String id) {
        this.userKey = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getTexte() {
        return texte;
    }

    public String getKeyInDatabase() {
        return keyInDatabase;
    }

    public void setKeyInDatabase(String keyInDatabase) {
        this.keyInDatabase = keyInDatabase;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public void setInDatabaseUrl(String inDatabaseUrl) {
        this.inDatabaseUrl = inDatabaseUrl;
    }

    public String getInDatabaseUrl() {
        return inDatabaseUrl;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public boolean isEqual(Services serv){
        System.out.println(serv);
        if(this.keyInDatabase==serv.getKeyInDatabase()){
            return true;
        }
        return false;
    }
}
