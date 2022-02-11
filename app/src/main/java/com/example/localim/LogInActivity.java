package com.example.localim;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LogInActivity  extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;

    String email;
    String password;

    private User user=new User();
    FirebaseAuth myFireAuth;

    private InterfaceForDataBase database=new InterfaceForDataBase("user");
    private String key;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_activity);

        this.emailEditText=(EditText) findViewById(R.id.emailLogIn);
        this.passwordEditText=(EditText) findViewById(R.id.passwordLogIn);

        this.myFireAuth=FirebaseAuth.getInstance();
    }
    public void logIn(View view){
    if(!verifEmail()|| !verifPassword()){
        return;
    }
    else{
        this.email=emailEditText.getText().toString();
        this.password=passwordEditText.getText().toString();
        loadData();
        this.myFireAuth.signInWithEmailAndPassword(this.email,this.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(myFireAuth.getCurrentUser().isEmailVerified()){

                        Toast.makeText(LogInActivity.this, "Vous etes connecter. Bonjour "+user.getNom(), Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(LogInActivity.this,MainActivity.class);
                        intent.putExtra("key",key);
                        startActivity(intent);

                    }
                    else{
                        emailEditText.setError("Pensez a verifier votre Email");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("This",e.getMessage());
                if(e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                    emailEditText.setError("Il n'y a aucun compte associer a cette adresse email");
                }
                else if(e.getMessage().equals("The email address is badly formatted.")){
                    emailEditText.setError("Votre Email doit etre bien former");
                }
                else if(e.getMessage().equals("The password is invalid or the user does not have a password.")){
                    passwordEditText.setError("Votre mot de passe est incorrect");
                }

            }
        });
    }

    }

    public  boolean verifEmail(){
        this.email=emailEditText.getText().toString();
        if(email.isEmpty()) {
            emailEditText.setError("Entrez votre email");
            return false;
        }
        else{
            emailEditText.setError(null);
            return true;
        }
    }
    public boolean verifPassword(){
        this.password=passwordEditText.getText().toString();
        if(password.isEmpty()) {
            passwordEditText.setError("Entrez votre mot de passe");
            return false;
        }
        else if(this.password.length()<=6 || this.password.length()>=10){
            passwordEditText.setError("Entrez un mot de passe entre 6 et 10 caractere");
            return false;
        }
        else{
            passwordEditText.setError(null);
            return true;
        }

    }
    private void loadData() {
        Query checkUser=this.database.getDatabaseReference().orderByChild("email").equalTo(this.email);
        this.database.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    User myUser=data.getValue(User.class);
                    if(myUser.getEmail().equals(email)){
                        user=myUser;
                        key=data.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void launchSignInActivity(View view){
                Intent toSignIn=new Intent(LogInActivity.this,SignInActivity.class);
                startActivity(toSignIn);

    }
}
