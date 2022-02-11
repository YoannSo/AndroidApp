package com.example.localim;

import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.lang.reflect.Type;

public class InterfaceForDataBase {
    private DatabaseReference databaseReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://localim-7ad0a-default-rtdb.europe-west1.firebasedatabase.app");
    User userReturned;
    boolean finish = false;

    public InterfaceForDataBase(String type) {
        if (type.equals("services"))
            this.databaseReference = database.getReference(Services.class.getSimpleName());
        else if (type.equals("user"))
            this.databaseReference = database.getReference(User.class.getSimpleName());

    }

    public Task<Void> add(Services service) {
        // if(service==null) //throw exception
        this.databaseReference = database.getReference(Services.class.getSimpleName());
        return this.databaseReference.push().setValue(service);
    }

    public Task<Void> addUser(User user) {
        this.databaseReference = database.getReference(User.class.getSimpleName());
        return this.databaseReference.push().setValue(user);
    }

    public Query get() {
        return this.databaseReference.orderByKey();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public User getUserReturned() {
        return userReturned;
    }

    public boolean isFinish() {
        return finish;
    }

    public void updateString(String champ,String newValue,String key){
        System.out.println(champ+" "+newValue+" "+key);
        this.databaseReference.child(key).child(champ).setValue(newValue);
    }
    public void updateCredit(int newValue,String key){
        System.out.println("c'est la cle"+key);
        this.databaseReference.child(key).child("credit").setValue(newValue);

    }

    public void remove(String key) {
        this.databaseReference.child(key).removeValue();
    }
}
