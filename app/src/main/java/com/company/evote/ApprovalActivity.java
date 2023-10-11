package com.company.evote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ApprovalActivity extends AppCompatActivity implements DbHelper.OnUserRoleResultListener {

    Button approveBtn, declineBtn, voteBtn;
    TextView nameTv, emailTv, phoneTv, schoolTv, positionTv, yearTv, genderTv;
    String userId;
    ImageView backBtn,imageView;
    private boolean backBtnClicked = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        DbHelper.checkUserRole(this);

        imageView = findViewById(R.id.imageView7);
        progressDialog = new ProgressDialog(this);
        nameTv = findViewById(R.id.tv_name);
        emailTv = findViewById(R.id.tv_email);
        phoneTv = findViewById(R.id.tv_phone);
        schoolTv = findViewById(R.id.tv_school);
        positionTv = findViewById(R.id.tv_deligate);
        yearTv = findViewById(R.id.tv_year);
        genderTv = findViewById(R.id.tv_gender);

        voteBtn = findViewById(R.id.voteBtn);
        backBtn = findViewById(R.id.back_btn);


        // Retrieve the passed data
        String emailToFind = getIntent().getStringExtra("EMAIL");
        TextView textV = findViewById(R.id.tv_email);
        textV.setText(emailToFind);
        FirebaseDatabase.getInstance().getReference("UserProfiles")
                .orderByChild("email")
                .equalTo(emailToFind)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                               userId = userSnapshot.getKey();
                               retrieveUserInfo(userId);
                                String imagePath = "images/" + userId + ".jpg";

                                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                StorageReference imageRef = storageReference.child(imagePath);

                                imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                        // Set the bitmap to the ImageView
                                      imageView.setImageBitmap(bitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.e("TAG", "Failed to retrieve image: " + exception.getMessage());
                                    }
                                });
                            }
                        } else {
                            // No user found with the provided email
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error
                    }
                });


        approveBtn = findViewById(R.id.approve);
        declineBtn = findViewById(R.id.decline);

        backBtn.setOnClickListener(v -> {
            backBtnClicked = true;
            DbHelper.checkUserRole(this);
        });
        approveBtn.setOnClickListener(v -> {
            changeUserStatus(userId, "accepted");
            Intent intent = new Intent(getApplicationContext(), ApplicantsActivity.class);
            startActivity(intent);
            finish();
        });

        declineBtn.setOnClickListener(v -> {
            changeUserStatus(userId, "declined");
            Intent intent = new Intent(getApplicationContext(), ApplicantsActivity.class);
            startActivity(intent);
            finish();
        });

        voteBtn.setOnClickListener(v -> {
            Intent vote = new Intent(getApplicationContext(),VoteVerificationActivity.class);
            vote.putExtra("VOTE", userId);
            startActivity(vote);
        });
    }

    private void showApprovalDialog() {
        progressDialog.setTitle("Approval");
        progressDialog.setMessage("submitting approval...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        progressDialog.show();
    }

    // Function to retrieve user information
    private void retrieveUserInfo(String userId) {
        DatabaseReference personalInfoRef = FirebaseDatabase.getInstance().getReference("Applicants").child(userId);
        personalInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String school = dataSnapshot.child("school").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String post = dataSnapshot.child("post").getValue(String.class);
                    String year = dataSnapshot.child("year").getValue(String.class);
                    String gender = dataSnapshot.child("gender").getValue(String.class);

                    nameTv.setText(name);
                    emailTv.setText(email);
                    schoolTv.setText(school);
                    phoneTv.setText(phone);
                    positionTv.setText(post);
                    yearTv.setText(year);
                    genderTv.setText(gender);
                } else {
                    // Data doesn't exist for the specified user ID
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
    private void changeUserStatus(String userId, String status) {
        showApprovalDialog();
        DatabaseReference applicantsRef = FirebaseDatabase.getInstance().getReference("Applicants").child(userId);
        applicantsRef.child("status").setValue(status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Successfully changed the name
                            // You can perform additional actions here if needed
                        } else {
                            progressDialog.dismiss();
                            // Failed to change the name
                            Exception exception = task.getException();
                            // Handle the exception
                        }
                    }
                });
    }
    public void changeActivity(Class<?> nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserRoleSuccess() {
        // User is not an admin, navigate to the UserActivity
        approveBtn.setVisibility(View.GONE);
        declineBtn.setVisibility(View.GONE);
        if (backBtnClicked){
            Intent intent = new Intent(getApplicationContext(), VoteActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onUserRoleAdmin() {
        // User is an admin, navigate to the AdminActivity
        voteBtn.setVisibility(View.GONE);
        if (backBtnClicked){
            Intent intent = new Intent(getApplicationContext(), ApplicantsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onUserRoleNotDefined() {

    }

    @Override
    public void onUserRoleError(DatabaseError error) {

    }

}