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

public class SignInActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneNumberEditText;

    String email;
    String password;
    String confirmPassword;
    private String firstName;
    private String phoneNumber;
    private String lastName;
    private int id=0;
    FirebaseAuth myFireAuth;
    InterfaceForDataBase databaseInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);
        this.emailEditText=(EditText) findViewById(R.id.emailSignIn);
        this.passwordEditText=(EditText) findViewById(R.id.passwordSignIn);
        this.confirmPasswordEditText=(EditText) findViewById(R.id.confirmPasswordSignIn);
        this.firstNameEditText=(EditText) findViewById(R.id.editTextFirstName);
        this.lastNameEditText=(EditText) findViewById(R.id.editTextLastName);
        this.phoneNumberEditText=(EditText) findViewById(R.id.editTextPhone);
        this.myFireAuth=FirebaseAuth.getInstance();
    }
    public void signIn(View view){
        if(!verifEmail() || !verifPassword() || !verifConfirmPassword() || !verifFirstName()|| !verifLastName()||!verifPhoneNumber()){
            return;
        }
        else{
            int min=1;int max=999999999;
            this.id = (int)Math.floor(Math.random()*(max-min+1)+min);

            this.databaseInterface=new InterfaceForDataBase("user");
            User newUser=new User(this.lastName,this.firstName,this.email,this.phoneNumber);

            this.myFireAuth.createUserWithEmailAndPassword(this.email,this.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        myFireAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    databaseInterface.addUser(newUser);
                                    Toast.makeText(SignInActivity.this, "Votre compte a été crée, vous allez recevoir un mail de confirmation", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d("This",e.getMessage());

                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("This",e.getMessage());
                    if(e.getMessage().equals("The email address is badly formatted.")){
                        emailEditText.setError("Votre Email doit etre bien former");
                    }
                }
            });
        }

    }

    private boolean verifPhoneNumber() {
        this.phoneNumber=phoneNumberEditText.getText().toString();
        if(phoneNumber.isEmpty()) {
            phoneNumberEditText.setError("Entrez votre numero de telephone");
            return false;
        }
        else if(phoneNumber.length()!=10){
            phoneNumberEditText.setError("Votre numeto de telephone doit contenir 10 chiffres");
            return false;
        }

        for(int i=0;i<phoneNumber.length();i++){
            if(phoneNumber.charAt(i)<'0'||phoneNumber.charAt(i)>'9'){
                phoneNumberEditText.setError("Votre numero de telephone doit contenir que des chiffres");
                return false;
            }
        }

            phoneNumberEditText.setError(null);
            return true;

    }

    private boolean verifLastName() {
        this.lastName=lastNameEditText.getText().toString();
        if(lastName.isEmpty()) {
            lastNameEditText.setError("Entrez votre nom");
            return false;
        }
        else{
            lastNameEditText.setError(null);
            return true;
        }
    }

    private boolean verifFirstName() {
        this.firstName=firstNameEditText.getText().toString();
        if(firstName.isEmpty()) {
            firstNameEditText.setError("Entrez votre prenom");
            return false;
        }
        else{
            firstNameEditText.setError(null);
            return true;
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
    boolean verifConfirmPassword(){
        this.confirmPassword=confirmPasswordEditText.getText().toString();
        if(confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Entrez votre confirmation de mot de passe");
            return false;
        }
        else if(!confirmPassword.equals(this.password)){
            confirmPasswordEditText.setError("Les deux mot de passes sont differents");
            return false;
        }
        else{
            confirmPasswordEditText.setError(null);
            return true;
        }
    }
    public void launchLogInActivity(View view){
        startActivity(new Intent(SignInActivity.this,LogInActivity.class));
    }
}
