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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private GridView simpleGrid;
    private Toolbar mainToolBar;
    private MenuItem profilButton;
    private Adapter myAdapter;
    private FloatingActionButton floatingButton;
    private SearchView searchView ;



    private ListeDeServices serviceList;
    private ServicesParcelable myServiceParcelable=new ServicesParcelable("","","","","","",0);
    private User userConnected;
    private String key;
    private ArrayList<Services> searchList;
    private int sizeList;


    private InterfaceForDataBase database;
    private DatabaseReference databaseRef= FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleGrid = (GridView) findViewById(R.id.servicesGrid);
        this.mainToolBar=(Toolbar)findViewById(R.id.mainToolBar);
        this.floatingButton=(FloatingActionButton)findViewById(R.id.floatingButton);

        setSupportActionBar(this.mainToolBar);
        this.searchView= (SearchView) findViewById(R.id.app_bar_search);


        serviceList=new ListeDeServices();
        this.searchList=new ArrayList<>();

        this.database= new InterfaceForDataBase("services");
        loadData();


        myAdapter=new Adapter(this,R.layout.services, serviceList.getServiceList());
        simpleGrid.setAdapter(myAdapter);


        simpleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int i= (int) id;
                if(searchList.size()!=serviceList.getServiceList().size() &&searchList.size()!=0){
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


                Intent intent = new Intent(MainActivity.this, serviceContentActivity.class);
                intent.putExtra("serviceExtra",myServiceParcelable);
                intent.putExtra("image",myServiceParcelable.getInDatabaseUrl());
                intent.putExtra("key",key);
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        this.database.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Services> temp=new ArrayList<>();
                for(DataSnapshot data:snapshot.getChildren()){
                    Services newService=data.getValue(Services.class);
                    newService.setKeyInDatabase(data.getKey());
                    temp.add(newService);
                    serviceList.addService(newService);
                }
                serviceList.remove(temp);
                sizeList=serviceList.getServiceList().size();
                myAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        Intent intent=getIntent();
         this.key=intent.getStringExtra("key");
        if(key!=null) {

                this.floatingButton.setVisibility(View.VISIBLE);
                Query  query = new InterfaceForDataBase("user").getDatabaseReference();
                Task<DataSnapshot> test=query.get();
                new InterfaceForDataBase("user").getDatabaseReference().child(key).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        userConnected=task.getResult().getValue(User.class);

                        System.out.println(mainToolBar.findViewById(R.id.profilButton));

                    }
                });


        }
        super.onResume();



    }
    public void addNewActivity(View view) {
        Intent intent = new Intent(MainActivity.this, addNewServiceActivity.class);
       intent.putExtra("key",this.key);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        SearchView searchView=(SearchView) mainToolBar.getMenu().getItem(0).getActionView();

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

    public void launchAuthentification(MenuItem item) {
        Intent intent=new Intent(MainActivity.this,LogInActivity.class);
        startActivity(intent);
    }

    public void openProfil(MenuItem item){
        if(this.key!=null){
        Intent intent=new Intent(MainActivity.this,profilActivity.class);
        intent.putExtra("key",this.key);
        startActivity(intent);
        }
        else{
            launchAuthentification(null);
        }
    }

    public void prendreLeService(View view) {
    }
}