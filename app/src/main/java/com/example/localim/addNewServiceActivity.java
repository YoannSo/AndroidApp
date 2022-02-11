package com.example.localim;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class addNewServiceActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 0; //variable pour la permission de prendre des images
    private static final int RESULT_LOAD_IMAGE = 1;

    private ServicesParcelable myServiceParcelable = new ServicesParcelable("", "", "","-1","", "",0);

    private Services serviceToDatabase;
    private EditText titreEditText;
    private EditText lieuEditText;
    private EditText descriptionEditText;
    private ImageView imageImported;
    private Uri imageUri;

    private DatabaseReference root=FirebaseDatabase.getInstance().getReference();
    private StorageReference storageRef=FirebaseStorage.getInstance().getReference();

    private RadioGroup radioGroup;

    private int cost;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_servce_activity);

        this.titreEditText = (EditText) findViewById(R.id.addServiceTitre); //init des variables xml
        this.lieuEditText = (EditText) findViewById(R.id.addServiceLieu);
        this.descriptionEditText = (EditText) findViewById(R.id.addServiceDescription);
        this.radioGroup=(RadioGroup) findViewById(R.id.radioGroup);

        imageImported = (ImageView) findViewById(R.id.imageImported);

        this.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { //radio button pour savoir le cost d'un service
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id=group.getCheckedRadioButtonId();
                if(id==R.id.radioButton1){
                    cost=1;
                }
                else if(id==R.id.radioButton2){
                    cost=2;

                }
                else if(id==R.id.radioButton3){
                    cost=3;

                }
            }
        });


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //savoir si on a la permission sinon on la demande
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }

    }

    public void importImage(View view) {
        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) { //cela va nous permettre de si on arrive a load l'image pouvoir la recuperer dans l'application
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(imageUri, filePath, null, null, null);
                    cursor.moveToFirst();
                    int columIndex = cursor.getColumnIndex(filePath[0]);
                    String picturePath = cursor.getString(columIndex);
                    cursor.close();
                    myServiceParcelable.setImage(BitmapFactory.decodeFile(picturePath));

                    imageImported.setImageBitmap(myServiceParcelable.getImage());
                }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void addService(View view) {


        this.myServiceParcelable.setLieu(lieuEditText.getText().toString());
        this.myServiceParcelable.setTexte(descriptionEditText.getText().toString());
        this.myServiceParcelable.setTitre(titreEditText.getText().toString());
        Intent myIntent=getIntent();
        uploadImage();
         Intent intent = new Intent(addNewServiceActivity.this, MainActivity.class);
        intent.putExtra("key",myIntent.getStringExtra("key"));
        startActivity(intent); // start Intent
    }

    private void uploadImage() {
        if(this.imageUri!=null){
        StorageReference fileRef= storageRef.child(System.currentTimeMillis()+"."+getFileExtesion());
        fileRef.putFile(this.imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        myServiceParcelable.setInDatabaseUrl(uri.toString());
                        serviceToDatabase = new Services(myServiceParcelable.getTitre(), myServiceParcelable.getTexte(),myServiceParcelable.getLieu(),myServiceParcelable.getInDatabaseUrl(),getIntent().getStringExtra("key"),cost);

                        new InterfaceForDataBase("services").add(serviceToDatabase);
                        Toast.makeText(addNewServiceActivity.this, "Votre service a été ajouter", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(addNewServiceActivity.this, "Echec d'upload de l'image", Toast.LENGTH_SHORT).show();
            }
        });
        }else{
            Toast.makeText(addNewServiceActivity.this, "Selectionner une image", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private String getFileExtesion() {
        ContentResolver cr =getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(this.imageUri));
    }

}

