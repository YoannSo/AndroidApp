package com.example.localim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class Adapter extends ArrayAdapter  { //Notre classe adapter qui va nous permettre d'afficher correctement notre gridView

    private ArrayList<Services> serviceList;


    public Adapter(@NonNull Context context, int resource, @NonNull ArrayList<Services> objects) {
        super(context, resource, objects);
        this.serviceList=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //Permet la creation de "cartes" dans la gridView
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.services, null);
        TextView textView = (TextView) v.findViewById(R.id.serviceText);
        ImageView imageView = (ImageView) v.findViewById(R.id.serviceImage);
        textView.setText(serviceList.get(position).getTitre());
        Glide.with(getContext()).load(serviceList.get(position).getInDatabaseUrl()).into(imageView);
        return v;
    }



}
