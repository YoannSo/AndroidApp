package com.example.localim;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ServicesParcelable extends Services implements Parcelable {
    public ServicesParcelable(String titre, String texte, String lieu,String userKey,String key,String url,int cost){

        super(titre,texte,lieu,url,userKey,key,cost);

    }


    protected ServicesParcelable(Parcel in) {

        super(in.readString(), in.readString(),in.readString(),in.readString(),in.readString(),in.readString(),in.readInt());

    }

    public static final Creator<ServicesParcelable> CREATOR = new Creator<ServicesParcelable>() {
        @Override
        public ServicesParcelable createFromParcel(Parcel in) {
            return new ServicesParcelable(in);
        }

        @Override
        public ServicesParcelable[] newArray(int size) {
            return new ServicesParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getTitre());
        parcel.writeString(this.getTexte());
        parcel.writeString(this.getLieu());
        parcel.writeString(this.getUserKey());
        parcel.writeString(this.getKeyInDatabase());
        parcel.writeString(this.getInDatabaseUrl());
        parcel.writeInt(this.getCost());



    }
}
