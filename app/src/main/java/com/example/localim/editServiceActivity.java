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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class editServiceActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST = 0;
    private static final int RESULT_LOAD_IMAGE = 1;
    private Uri imageUri;
    private ImageView imageImported;
    private DatabaseReference root= FirebaseDatabase.getInstance().getReference();
    private StorageReference storageRef= FirebaseStorage.getInstance().getReference();

    private EditText titre;
    private EditText lieu;
    private EditText description;
    private ImageView image;

    private String lastTitre;
    private String lastLieu;
    private String lastDescription;
    private boolean newImage;
    ServicesParcelable result;
    private String key;
    private String url;
    private boolean imageChanged=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_service_activity);

         titre = (EditText) findViewById(R.id.editServiceTitre);
         lieu = (EditText) findViewById(R.id.editServiceLieu);
         description = (EditText) findViewById(R.id.editServiceDescription);
         image = (ImageView) findViewById(R.id.editServiceImage);

         Intent intent= getIntent();
         result=(ServicesParcelable) intent.getParcelableExtra("serviceExtra");
         url=(String) intent.getStringExtra("url");
         this.key=(String) intent.getStringExtra("key");

        this.lastTitre=result.getTitre();
        this.lastLieu=result.getLieu();
        this.lastDescription=result.getTexte();

        titre.setText(this.lastTitre);
        lieu.setText(this.lastLieu);
        description.setText(this.lastDescription);

        Glide.with(this).load(url).into(image);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
        }
    }


    public void Supprimer(View view) {
        new InterfaceForDataBase("services").remove(this.key);
        Toast.makeText(editServiceActivity.this, "Votre service a été supprimer", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(editServiceActivity.this, profilActivity.class);
        intent.putExtra("key",getIntent().getStringExtra("userKey"));
        intent.putExtra("id",getIntent().getIntExtra("userId",-1));
        startActivity(intent);

    }

    public void Editer(View view) {
        if(!titre.getText().toString().equals(this.lastTitre)){
            new InterfaceForDataBase("services").updateString("titre",titre.getText().toString(),this.key);
        }
        if(!lieu.getText().toString().equals(this.lastLieu)){
            new InterfaceForDataBase("services").updateString("lieu",lieu.getText().toString(),this.key);

        }if(!description.getText().toString().equals(this.lastDescription)){
            new InterfaceForDataBase("services").updateString("texte",description.getText().toString(),this.key);
        }
        if(imageChanged){
            uploadImage();

            StorageReference delete=FirebaseStorage.getInstance().getReferenceFromUrl(url);

            delete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    System.out.println("IMAGE SUPPRIMER");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.toString());

                }
            });
        }

        Intent intent = new Intent(editServiceActivity.this, profilActivity.class);
        intent.putExtra("key", getIntent().getStringExtra("userKey"));
        intent.putExtra("id", getIntent().getIntExtra("userId", -1));
        startActivity(intent);

    }

    public void changeImage(View view) {
        Intent myIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(myIntent, RESULT_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    imageUri = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(imageUri, filePath, null, null, null);
                    cursor.moveToFirst();
                    int columIndex = cursor.getColumnIndex(filePath[0]);
                    String picturePath = cursor.getString(columIndex);
                    cursor.close();
                    imageImported = (ImageView) findViewById(R.id.imageImported);
                    imageChanged=true;
                    image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private String getFileExtesion() {
        ContentResolver cr =getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(this.imageUri));
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
                            new InterfaceForDataBase("services").updateString("inDatabaseUrl",uri.toString(),key);
                            String id=root.push().getKey();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(editServiceActivity.this, "Echec d'upload de l'image", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(editServiceActivity.this, "Selectionner une image", Toast.LENGTH_SHORT).show();
            return;
        }

    }

}