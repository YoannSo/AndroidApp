package com.example.localim;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class profilActivity extends AppCompatActivity {
    private Toolbar mainToolBar;
    private GridView simpleGrid;
    private Adapter myAdapter;
    private SearchView searchView ;



    private ListeDeServices serviceList;
    private ServicesParcelable myServiceParcelable=new ServicesParcelable("", "", "","","", "",0);
    private String userKey;
    private ArrayList<Services> searchList;

    private InterfaceForDataBase database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_activity);

        simpleGrid = (GridView) findViewById(R.id.servicesGridProfil);
        this.mainToolBar = (Toolbar) findViewById(R.id.mainToolBarProfil);
        simpleGrid = (GridView) findViewById(R.id.servicesGridProfil);
        this.mainToolBar=(Toolbar)findViewById(R.id.mainToolBarProfil);

        setSupportActionBar(this.mainToolBar);

        serviceList = new ListeDeServices();
        this.searchList=new ArrayList<>();

        this.database= new InterfaceForDataBase("services");
        Intent intent=getIntent();
        this.userKey=intent.getStringExtra("key");
        loadData();


        myAdapter=new Adapter(this,R.layout.services, serviceList.getServiceList());
        simpleGrid.setAdapter(myAdapter);
        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i= (int) id;
                if(searchList.size()!=serviceList.getServiceList().size()&&searchList.size()!=0){
                    myServiceParcelable.setImage(searchList.get(i).getImage());
                    myServiceParcelable.setTexte(searchList.get(i).getTexte());
                    myServiceParcelable.setLieu(searchList.get(i).getLieu());
                    myServiceParcelable.setTitre(searchList.get(i).getTitre());
                    myServiceParcelable.setInDatabaseUrl(searchList.get(i).getInDatabaseUrl());
                    myServiceParcelable.setId(searchList.get(i).getId());
                    myServiceParcelable.setKeyInDatabase(searchList.get(i).getKeyInDatabase());
                }
                else {
                    myServiceParcelable.setImage(serviceList.getServiceList().get(i).getImage());
                    myServiceParcelable.setTexte(serviceList.getServiceList().get(i).getTexte());
                    myServiceParcelable.setLieu(serviceList.getServiceList().get(i).getLieu());
                    myServiceParcelable.setTitre(serviceList.getServiceList().get(i).getTitre());
                    myServiceParcelable.setInDatabaseUrl(serviceList.getServiceList().get(i).getInDatabaseUrl());
                    myServiceParcelable.setId(serviceList.getServiceList().get(i).getId());
                    myServiceParcelable.setKeyInDatabase(serviceList.getServiceList().get(i).getKeyInDatabase());
                }

                Intent intent = new Intent(profilActivity.this, editServiceActivity.class);
                intent.putExtra("serviceExtra",myServiceParcelable);
                intent.putExtra("url",myServiceParcelable.getInDatabaseUrl());
                intent.putExtra("key",myServiceParcelable.getKeyInDatabase());
                intent.putExtra("userKey",userKey);



                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profil_menu,menu);
        this.searchView=(SearchView) mainToolBar.getMenu().getItem(1).getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });
        return true;
    }
    public void search(String s){
        this.searchList.clear();
        for(Services ser:this.serviceList.getServiceList()){
            if(ser.getTitre().toLowerCase().contains(s.toLowerCase())){
                searchList.add(ser);
            }
        }
        this.myAdapter=new Adapter(this,R.layout.services,searchList);
        this.simpleGrid.setAdapter(this.myAdapter);
    }
    public void loadData(){
        this.database.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Services> temp=new ArrayList<Services>();

                for(DataSnapshot data:snapshot.getChildren()){
                    Services newService=data.getValue(Services.class);
                    newService.setKeyInDatabase(data.getKey());
                    if(newService.getUserKey().equals(userKey)) {
                        serviceList.addService(newService);
                        temp.add(newService);
                    }
                }
                serviceList.remove(temp);

                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void goToMainMenu(MenuItem item) {
        Intent intent=new Intent(profilActivity.this,MainActivity.class);
        intent.putExtra("key",this.userKey);
        startActivity(intent);
    }

    public void deconnexion(MenuItem item) {
        Intent intent=new Intent(profilActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void editProfil(MenuItem item) {
        Intent intent=new Intent(profilActivity.this,editProfilActivity.class);
        intent.putExtra("key",this.userKey);
        startActivity(intent);
    }
}
