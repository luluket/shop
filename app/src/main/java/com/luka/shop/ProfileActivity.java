package com.luka.shop;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextView banner;
    FirebaseAuth mAuth;
    ImageView profileImg;
    Button changeProfileImg, logout;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Instantiate widgets
        banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        TextView fullName = findViewById(R.id.fullName);
        TextView email = findViewById(R.id.email);
        TextView age = findViewById(R.id.age);
        profileImg = findViewById(R.id.profileImg);

        changeProfileImg = findViewById(R.id.changeProfileImg);
        changeProfileImg.setOnClickListener(this);

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        String userId = mAuth.getCurrentUser().getUid();

        // load authenticated user profile image
        StorageReference profileRef = mStorageRef.child("users/" + userId + "/profile");
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImg));

        // set authenticated user data fields
        FirebaseFirestore.getInstance().collection("users").document(userId).addSnapshotListener((value, error) -> {
            assert value != null;
            age.setText(value.getString("age"));
            fullName.setText(value.getString("fullName"));
            email.setText(value.getString("email"));
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banner:
                finish();
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
        mAuth.signOut(); // firebase auth sign out

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // clears all activity previous to ProfileActivity
        finish(); // clears ProfileActivity
        startActivity(intent);

    }

    private void changeImage() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload to storage
        StorageReference fileRef = mStorageRef.child("users/" + mAuth.getCurrentUser().getUid() + "/profile"); // overwriting existing image
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImg)))
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Image upload failed", Toast.LENGTH_LONG).show());
    }
}