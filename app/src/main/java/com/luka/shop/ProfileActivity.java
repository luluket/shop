package com.luka.shop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    TextView fullName,email,age,banner;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;
    ProgressBar progressBar;
    ImageView profileImg;
    Button changeProfileImg, logout;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        banner=findViewById(R.id.banner);
        banner.setOnClickListener(this);

        fullName=findViewById(R.id.fullName);
        email=findViewById(R.id.email);
        age=findViewById(R.id.age);
        profileImg = findViewById(R.id.profileImg);
        changeProfileImg = findViewById(R.id.changeProfileImg);
        progressBar=findViewById(R.id.progressBar);

        logout=findViewById(R.id.logout);
        logout.setOnClickListener(this);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();

        userId=mAuth.getCurrentUser().getUid();

        StorageReference profileRef = mStorageRef.child("users/" + userId + "/profile");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImg);
            }
        });

        DocumentReference mRef=db.collection("users").document(userId);
        mRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                age.setText(value.getString("age"));
                fullName.setText(value.getString("fullName"));
                email.setText(value.getString("email"));
            }
        });

        changeProfileImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.banner:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                break;
            case R.id.changeProfileImg:
                changeImage();
                break;
            case R.id.logout:
                logout();
                break;
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

    private void changeImage() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode== Activity.RESULT_OK){
                Uri imageUri = data.getData();
                progressBar.setVisibility(View.VISIBLE);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload to storage

        StorageReference fileRef = mStorageRef.child("users/" + mAuth.getCurrentUser().getUid() + "/profile"); // overwriting existing image
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImg);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this,"Image upload failed",Toast.LENGTH_LONG).show();
            }
        });
        progressBar.setVisibility(View.GONE);
    }
}