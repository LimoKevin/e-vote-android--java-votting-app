package com.company.evote;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class RecievedFragment extends Fragment {
    TextView emailTv, resendOTP;
    EditText code1;
    FirebaseAuth mAuth;
    String userEmail,code;
    Button verify;
    String generatedOTP, value;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recieved, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
             value = bundle.getString("key");
//            Toast.makeText(getContext(),"love", Toast.LENGTH_SHORT).show();
        }
        mAuth = FirebaseAuth.getInstance();
        userEmail = mAuth.getCurrentUser().getEmail();

        code1 = view.findViewById(R.id.code1);
        verify = view.findViewById(R.id.verify);
        emailTv = view.findViewById(R.id.tv_emailV);
        resendOTP = view.findViewById(R.id.resend_otp);


        emailTv.setText(userEmail);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOTPVerification();

            }
        });

        resendOTP.setOnClickListener(v -> sendMail());
        return view;
    }
    private void checkOTPVerification() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference otpRef = database.getReference("UserProfiles").child(userId).child("OTPVerification");


            otpRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                         String code = code1.getText().toString();
                         String otpValue = dataSnapshot.getValue(String.class);

                        if (otpValue.equals(code)) {
                            setTheVoteValue(value);
                            Toast.makeText(getContext(), "verification success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "invalid verification code!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // OTP node does not exist
                        Toast.makeText(getContext(), "OTP has expired", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle database error
                }
            });
        } else {
            // User is not authenticated
        }
    }

    private void sendMail() {
        FirebaseUser user = mAuth.getCurrentUser();
        generatedOTP = generateOTP(6);
        String email = user.getEmail();
        String subject = "OTP Verification";
        String message = "Enter this OTP to verify   " + generatedOTP;
        long expiryTimeInMillis = System.currentTimeMillis() + (5 * 60 * 1000); // Expiry time is set to 5 minutes from now

        // Save OTP to the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseRef.child("UserProfiles").child(userId).child("OTPVerification").setValue(generatedOTP);
        }

        scheduleNodeDeletion(user.getUid(),expiryTimeInMillis);

        //send mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(getContext(), email,subject,message);
        javaMailAPI.execute();
    }

    private String generateOTP(int otpLength) {
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < otpLength; i++) {
            otp.append(characters.charAt(random.nextInt(characters.length())));
        }

        return otp.toString();
    }
    private void scheduleNodeDeletion(String userId, long expiryTimeInMillis) {
        long currentTime = System.currentTimeMillis();

        // Calculate the delay until expiry in milliseconds
        long delayUntilExpiry = expiryTimeInMillis - currentTime;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if OTP has expired
                if (System.currentTimeMillis() >= expiryTimeInMillis) {
                    // OTP has expired, delete the node
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    databaseRef.child("UserProfiles").child(userId).child("OTPVerification").removeValue();
                }
            }
        }, delayUntilExpiry);
    }

    private void setTheVoteValue(String valu){
        // Assuming you have a reference to your database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAth = FirebaseAuth.getInstance();
        String UserId = mAth.getCurrentUser().getUid();
        // Assuming you have a node named "voting" with a child named "vote"
        databaseRef.child("Voting").child(UserId).child("vote").setValue(valu)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            progressDialog.dismiss();
                            startActivity(new Intent(getActivity(),VoteActivity.class));
                            Toast.makeText(getContext(), "Vote casted successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to cast vote. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}