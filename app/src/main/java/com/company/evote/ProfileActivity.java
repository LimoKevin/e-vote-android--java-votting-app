package com.company.evote;


import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import com.squareup.picasso.Picasso;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity  implements View.OnClickListener,DbHelper.OnUserRoleResultListener {
    ConstraintLayout expandSecurity;
    LinearLayout linearLayout;
    ImageView dropSecurity, dropMenuInfo, profileImage;
    CardView cardView, cardViewInfo, cardView5;

    TextView tvEdit, acc_ver, tvName, tvPhone, tvEmail, tvRegno, tvSchool, tvYear, Uemail;

//    private static final int REQUEST_CODE = 101;
//    private StorageReference storageRef;
//    private String userId;
//    private static final int PERMISSION_REQUEST_CODE = 123;

    private static final int REQUEST_STORAGE_PERMISSION = 201;
    private static final int REQUEST_PICK_IMAGE = 202;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView arrback = findViewById(R.id.arrback);
        arrback.setOnClickListener(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        profileImage = findViewById(R.id.circleImageView);
        dropMenuInfo = findViewById(R.id.dropmenuInfo);
        cardViewInfo = findViewById(R.id.cardViewInfo);
        linearLayout = findViewById(R.id.linearLayout);
        cardView5 = findViewById(R.id.cardView5);

        expandSecurity = findViewById(R.id.expandablesecurity);
        dropSecurity = findViewById(R.id.dropsecurity);
        cardView = findViewById(R.id.expandCard);
        tvEdit = findViewById(R.id.edit_profile);
        acc_ver = findViewById(R.id.acc_ver);


        profileImage.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        acc_ver.setOnClickListener(this);
        dropSecurity.setOnClickListener(this);
        dropMenuInfo.setOnClickListener(this);
        cardView5.setOnClickListener(this);

        updatePersonalDetails();
        loadImageFromFirebaseStorage(currentUserId+".jpg",profileImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arrback:
                DbHelper.checkUserRole(this);
                break;
            case R.id.circleImageView:
//                requestStoragePermission();
                openGallery();
//                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//
//                } else {
//                    requestStoragePermission();
//                }
                break;
            case R.id.dropsecurity:
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                linearLayout.setVisibility(View.GONE);
                if (expandSecurity.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandSecurity.setVisibility(View.VISIBLE);
                    dropSecurity.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                } else {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    expandSecurity.setVisibility(View.GONE);
                    dropSecurity.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
                }
                Toast.makeText(this, "security arrow clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.edit_profile:
                Intent inten = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(inten);
                break;

            case R.id.acc_ver:
                Intent intent = new Intent(ProfileActivity.this, VerificationActivity.class);
                startActivity(intent);
                break;

            case R.id.dropmenuInfo:
                TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                expandSecurity.setVisibility(View.GONE);

                if (linearLayout.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    linearLayout.setVisibility(View.VISIBLE);
                    dropMenuInfo.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                } else {
                    TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                    linearLayout.setVisibility(View.GONE);
                    dropMenuInfo.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
                }
                break;

            case R.id.cardView5:
                logOut();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }

    private void updatePersonalDetails() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference personalInfoRef = database.getReference().child("personalInfo");
        DatabaseReference userProfileRef = database.getReference("UserProfiles");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = mAuth.getCurrentUser().getEmail();


        tvName = findViewById(R.id.per_name);
        tvEmail = findViewById(R.id.tvemail);
        tvPhone = findViewById(R.id.tvphone);
        tvRegno = findViewById(R.id.tvRegno);
        tvSchool = findViewById(R.id.tvschool);
        tvYear = findViewById(R.id.tvyear);
        Uemail = findViewById(R.id.emailTv);
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userProfileRef.child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.getValue(String.class);
                        TextView Uname = findViewById(R.id.nameTv);
                        tvName.setText(name);
                        Uname.setText(name);
                    } else {
                        // The user or the "name" field doesn't exist.
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors here
                }
            });
        }

        personalInfoRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming your "personalInfo" table has fields "name", "email", and "phone"
                    String email = mAuth.getCurrentUser().getEmail();
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String regNo = dataSnapshot.child("regNo").getValue(String.class);
                    String School = dataSnapshot.child("school").getValue(String.class);
                    String Year = dataSnapshot.child("year").getValue(String.class);

                    tvEmail.setText(email);
                    Uemail.setText(email);
                    tvPhone.setText(phone);
                    tvRegno.setText(regNo);
                    tvSchool.setText(School);
                    tvYear.setText(Year);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if necessary
            }
        });

    }

    private void logOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mAuth.signOut();
            Intent intr = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intr);
            finish();
        } else {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            Intent inti = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(inti);
            finish();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getApplicationContext(), "Storage permission is required to pick images", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                saveImageToFirebaseStorage(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageToFirebaseStorage(Uri imageUri) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        String userId = user.getUid();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef1 = storageRef.child("images/"+userId+".jpg");

        UploadTask uploadTask1 = imageRef1.putFile(imageUri);
        uploadTask1.addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
            Log.e("FirebaseStorageError", "Error uploading image: " + e.getMessage(), e);
        });
    }

    private void loadImageFromFirebaseStorage(String imageName, final ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + imageName);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use an image loading library (e.g., Picasso, Glide) to load the image into the ImageView
                Picasso.get().load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors that may occur
                Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onUserRoleSuccess() {
        // User is not an admin, navigate to the UserActivity
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserRoleAdmin() {
        // User is an admin, navigate to the AdminActivity
        Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserRoleNotDefined() {

    }

    @Override
    public void onUserRoleError(DatabaseError error) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DbHelper.checkUserRole(this);
    }
}