package com.company.evote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HelpActivity extends AppCompatActivity {
    private static final int CALL_PHONE_REQUEST_CODE = 123;
    ImageView callButton, emailButton,arrBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

       callButton = findViewById(R.id.call_icon);
       emailButton = findViewById(R.id.email_icon);
        ImageView arrback = findViewById(R.id.back_btn);
        arrback.setOnClickListener(v -> {
            changeActivity(HomeActivity.class);
        });

        BottomNavigationView bottomNavigationView= findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference("Dates").child("today");
                    datesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String eventType = dataSnapshot.getValue(String.class);
                                if ("results".equals(eventType)) {
                                    changeActivity(ResultsActivity.class);
                                } else{
                                    changeActivity(HomeActivity.class);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                    break;
                case R.id.vote:
                    changeActivity(VoteActivity.class);
                    break;
                case R.id.profile:
                    changeActivity(ProfileActivity.class);
                    break;
                case R.id.help:
                    break;
            }

            return true;
        });
       emailButton.setOnClickListener(v -> {
         openEmailApp();
       });

       callButton.setOnClickListener(v -> {
            // Check if the app has the CALL_PHONE permission
            if (ContextCompat.checkSelfPermission(HelpActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                // Request the CALL_PHONE permission
                ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_REQUEST_CODE);
            }
        });
    }


    private void makePhoneCall() {
        String phoneNumber = "0701599565";
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PHONE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, make the phone call
                makePhoneCall();
            } else {
                // Permission denied, show a message or handle as needed
                Toast.makeText(this, "Permission denied to make a phone call", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openEmailApp() {
        String recipientEmail = "support.evote@gmail.com"; // Replace with the actual email address
        String subject = "Support"; // Replace with your desired subject
        String body = "Body of the email"; // Replace with your desired body

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        PackageManager packageManager = getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, "Send email using:"));
        } else {
            Toast.makeText(this, "No email app available", Toast.LENGTH_SHORT).show();
        }
    }
    public void changeActivity(Class<?> nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference("Dates").child("today");
        datesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String eventType = dataSnapshot.getValue(String.class);
                    if ("results".equals(eventType)) {
                        changeActivity(ResultsActivity.class);
                    } else{
                        changeActivity(HomeActivity.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        super.onBackPressed();
    }
}