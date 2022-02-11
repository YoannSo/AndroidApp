package com.example.localim;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Objects;

// Gestion des swipes effectué suivant le tutoriel suivant :
// https://www.youtube.com/watch?v=sIh4FmkfdFA&ab_channel=DominiqueLiard

public class serviceContentActivity extends AppCompatActivity {
    private ListeDeServices serviceList;
    private InterfaceForDataBase database;
    ServicesParcelable content;

    DatabaseReference databaseRef;
    FirebaseAuth myFireAuth;

    private SwipeGestureDetector gestureDetector;

    private String userKey;
    private int userCredit;
    private int serviceCredit;
    private String userKeyFromService;
    private String serviceKey;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_content_activity);
        TextView titre = (TextView) findViewById(R.id.serviceContentTitre);
        TextView lieu = (TextView) findViewById(R.id.serviceContentLieu);
        TextView texte = (TextView) findViewById(R.id.serviceContentDescription);
        ImageView image = (ImageView) findViewById(R.id.serviceContentImage);

        Intent intent= getIntent();
        content=(ServicesParcelable) intent.getParcelableExtra("serviceExtra");
        String url=intent.getStringExtra("image");
        System.out.println("lalal "+url);
        this.userKey=intent.getStringExtra("key");
        this.userCredit=intent.getIntExtra("credit",-1);
        this.userKeyFromService=this.content.getInDatabaseUrl();
        this.serviceKey=this.content.getUserKey();
        this.serviceCredit=content.getCost();
        titre.setText(content.getTitre());
        lieu.setText(content.getLieu());
        texte.setText(content.getTexte());

        this.key=intent.getStringExtra("key");
        Glide.with(this).load(url).into(image);

        serviceList=new ListeDeServices();
        gestureDetector = new SwipeGestureDetector(this);
        this.loadData();

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        return gestureDetector.onTouchEvent(event);
    }

    public void prendreLeService(View view) {

        if (this.userCredit>this.serviceCredit){
            if(this.userKeyFromService.equals(this.userKey)){
                Toast.makeText(this, "vous ne pouvez pas prendre votre Service", Toast.LENGTH_SHORT).show();
            }
            else {
                new InterfaceForDataBase("user").updateCredit(this.userCredit - serviceCredit, this.userKey);
                new InterfaceForDataBase("user").updateCredit(this.userCredit + serviceCredit, this.userKeyFromService);
                new InterfaceForDataBase("services").remove(this.serviceKey);
                String s = "Vous avez pris ce service vous avez perdu " + Integer.toString(serviceCredit) + " credit, il vous en reste:" + Integer.toString(this.userCredit - serviceCredit);
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            String s="Vous n'avez pas assez de credit, vous en avez seulement "+Integer.toString(serviceCredit);
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
        Intent intent=new Intent(serviceContentActivity.this,MainActivity.class);
        intent.putExtra("key",this.userKey);
        startActivity(intent);
    }
    // direction = 1 pour gauche || 2 pour droite
    public void onSwipe(int direction){

        switch (direction){
            case 1:
                for (int i=1;i< serviceList.getServiceList().size();i++){
                    if (Objects.equals(serviceList.getServiceList().get(i).getTitre(), content.getTitre())){

                        ServicesParcelable myServiceParcelable=new ServicesParcelable("","","","","","",0);
                        myServiceParcelable.setId(serviceList.getServiceList().get(i-1).getId());
                        myServiceParcelable.setImage(serviceList.getServiceList().get(i-1).getImage());
                        myServiceParcelable.setTexte(serviceList.getServiceList().get(i-1).getTexte());
                        myServiceParcelable.setLieu(serviceList.getServiceList().get(i-1).getLieu());
                        myServiceParcelable.setTitre(serviceList.getServiceList().get(i-1).getTitre());
                        myServiceParcelable.setInDatabaseUrl(serviceList.getServiceList().get(i-1).getInDatabaseUrl());

                        Intent intent = new Intent(serviceContentActivity.this, serviceContentActivity.class);
                        intent.putExtra("serviceExtra",myServiceParcelable);
                        intent.putExtra("image",myServiceParcelable.getInDatabaseUrl());
                        intent.putExtra("key",key);
                        startActivity(intent);
                        break;
                    }

                }
                break;
            case 2:
                for (int i=0;i< serviceList.getServiceList().size()-1;i++){
                    if (Objects.equals(serviceList.getServiceList().get(i).getTitre(), content.getTitre())){

                        ServicesParcelable myServiceParcelable=new ServicesParcelable("","","","","","",0);
                        myServiceParcelable.setId(serviceList.getServiceList().get(i+1).getId());
                        myServiceParcelable.setImage(serviceList.getServiceList().get(i+1).getImage());
                        myServiceParcelable.setTexte(serviceList.getServiceList().get(i+1).getTexte());
                        myServiceParcelable.setLieu(serviceList.getServiceList().get(i+1).getLieu());
                        myServiceParcelable.setTitre(serviceList.getServiceList().get(i+1).getTitre());
                        myServiceParcelable.setInDatabaseUrl(serviceList.getServiceList().get(i+1).getInDatabaseUrl());


                        // set an Intent to Another Activity
                        Intent intent = new Intent(this, serviceContentActivity.class);
                        intent.putExtra("serviceExtra",myServiceParcelable);
                        intent.putExtra("image",myServiceParcelable.getInDatabaseUrl());
                        intent.putExtra("key",key);
                        startActivity(intent);
                        break;
                    }

                }
                break;
        }
    }

    private void loadData() {
        database= new InterfaceForDataBase("services");
        database.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Services> temp=new ArrayList<Services>();

                for(DataSnapshot data:snapshot.getChildren()){
                    Services newService=data.getValue(Services.class);
                    newService.setKeyInDatabase(data.getKey());
                    temp.add(newService);
                    serviceList.addService(newService);
                }
                serviceList.remove(temp);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
