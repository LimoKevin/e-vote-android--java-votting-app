package com.company.evote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatesActivity extends AppCompatActivity{
    Button openDatePickerButton;
    Calendar calendar;

    TextView votingDate, applicationDate, resultsDate;
    private ImageView backBtn;
    DatabaseReference databaseReference;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates);

        backBtn = findViewById(R.id.back_btn);
        votingDate = findViewById(R.id.tv_Voting_date);
        applicationDate = findViewById(R.id.tv_application_date);
        resultsDate = findViewById(R.id.tv_results_date);

        databaseReference = FirebaseDatabase.getInstance().getReference("personalInfo");
        openDatePickerButton = findViewById(R.id.openDatePickerButton);
        calendar = Calendar.getInstance();

        applicationDate.setOnClickListener(v -> {
            pickDate(applicationDate);
        });

        votingDate.setOnClickListener(v -> {
            pickDate(votingDate);
        });
        resultsDate.setOnClickListener(v -> {
            pickDate(resultsDate);
        });
        openDatePickerButton.setOnClickListener(v -> {
            showProgressDialog();
            saveTimestampToDatabase();
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(AdminHomeActivity.class);
            }
        });
        BottomNavigationView bottomNavigationView= findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    changeActivity(AdminHomeActivity.class);
                    break;
                case R.id.applicants:
                    changeActivity(ApplicantsActivity.class);
                    break;
                case R.id.dates:
                    break;
                case R.id.profile:
                    changeActivity(ProfileActivity.class);
                    break;
            }

            return true;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        changeActivity(AdminHomeActivity.class);
    }

    public void changeActivity(Class<?> nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        startActivity(intent);
        finish();
    }
    private void pickDate(TextView textView) {
        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, year, monthOfYear, dayOfMonth) -> {
            // Create a Calendar instance to convert selected date to timestamp
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);

            // Get the timestamp from the selected date
            long timestamp = calendar.getTimeInMillis();

            // Convert timestamp to SimpleDateFormat (dd/MM/yyyy) format
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = sdf.format(new Date(timestamp));

            textView.setText(formattedDate);

            // Call the function to save the timestamp with a node name
//            saveTimestampToDatabase("votingDate", formattedDate);
        }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        // Show the DatePickerDialog
        datePickerDialog.show();
    }


    private void saveTimestampToDatabase() {
        String appDate = applicationDate.getText().toString();
        String voteDate = votingDate.getText().toString();
        String resultDate = resultsDate.getText().toString();
        // Save the timestamp to the Firebase Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Dates");
        databaseRef.child("application").setValue(appDate);
//        databaseRef.child("voting").setValue(voteDate);
        databaseRef.child("results").setValue(resultDate);

        databaseRef.child("voting").setValue(voteDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Voting Date saved successfully", Toast.LENGTH_SHORT).show();
                            dismissProgressDialog();

                            startActivity(new Intent(getApplicationContext(), AdminHomeActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to save Voting Date", Toast.LENGTH_SHORT).show();
                            dismissProgressDialog();
                        }
                    }
                });
    }



    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving");
        progressDialog.setMessage("Saving dates...");
        progressDialog.setCancelable(false); // Prevent user from dismissing dialog
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}