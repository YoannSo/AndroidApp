package com.example.localim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

public class editProfilActivity extends AppCompatActivity {

    private EditText prenom;
    private EditText nom;
    private EditText numero;

    private String ancienPrenom;
    private String ancienNom;
    private String ancienNum;


    private User currentUser;
    private String userKey;

    private boolean edited=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profil_activity);
        this.prenom=(EditText) findViewById(R.id.editProfilPrenom);
        this.nom=(EditText) findViewById(R.id.editProfilNom);
        this.numero=(EditText) findViewById(R.id.editProfilNum);
        ancienNom=nom.getText().toString();
        ancienPrenom=prenom.getText().toString();
        ancienNum=numero.getText().toString();

        this.userKey=getIntent().getStringExtra("key");
        if(userKey!=null) {


            Query query = new InterfaceForDataBase("user").getDatabaseReference();
            Task<DataSnapshot> test=query.get();

            new InterfaceForDataBase("user").getDatabaseReference().child(userKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    currentUser=task.getResult().getValue(User.class);
                    prenom.setText(currentUser.getPrenom());
                    nom.setText(currentUser.getNom());
                    numero.setText(currentUser.getNumTel());

                }
            });


        }

    }

    public void Editer(View view) {

        if (!nom.getText().toString().equals(this.ancienNom) && verifLastName()) {
            new InterfaceForDataBase("user").updateString("nom", nom.getText().toString(), this.userKey);
            this.edited = true;
        }
        if (!prenom.getText().toString().equals(this.ancienPrenom) && verifFirstName()) {
            new InterfaceForDataBase("user").updateString("prenom", prenom.getText().toString(), this.userKey);
            this.edited = true;
        }
        if (!numero.getText().toString().equals(this.ancienNum) && verifPhoneNumber()) {
            new InterfaceForDataBase("user").updateString("numTel", numero.getText().toString(), this.userKey);
            this.edited = true;
        }
        if (edited) {
            Toast.makeText(editProfilActivity.this, "Vos données ont été modifier", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(editProfilActivity.this, profilActivity.class);
            intent.putExtra("key",this.userKey);
            startActivity(intent);
        }
    }
    private boolean verifPhoneNumber() {
      String  phoneNumber=numero.getText().toString();
        if(phoneNumber.isEmpty()) {
            numero.setError("Entrez votre numero de telephone");
            return false;
        }
        else if(phoneNumber.length()!=10){
            numero.setError("Votre numeto de telephone doit contenir 10 chiffres");
            return false;
        }

        for(int i=0;i<phoneNumber.length();i++){
            if(phoneNumber.charAt(i)<'0'||phoneNumber.charAt(i)>'9'){
                numero.setError("Votre numero de telephone doit contenir que des chiffres");
                return false;
            }
        }

        numero.setError(null);
        return true;

    }

    private boolean verifLastName() {
        String lastName=nom.getText().toString();
        if(lastName.isEmpty()) {
            nom.setError("Entrez votre nom");
            return false;
        }
        else{
            nom.setError(null);
            return true;
        }
    }

    private boolean verifFirstName() {
        String firstName=prenom.getText().toString();
        if(firstName.isEmpty()) {
            prenom.setError("Entrez votre prenom");
            return false;
        }
        else{
            prenom.setError(null);
            return true;
        }
    }
    }

