package com.company.evote;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class SendOTPFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Button sendOTPButton;
    private TextView emailEdt;
    String generatedOTP;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sendotp, container, false);

        sendOTPButton = view.findViewById(R.id.sendOTP);
        emailEdt = view.findViewById(R.id.emailEdt);
        FirebaseUser user = mAuth.getCurrentUser();
        String uEmail = user.getEmail();
        emailEdt.setText(uEmail);


        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Bundle bundle = getArguments();
                if (bundle != null) {
                    String value = bundle.getString("key");

                    Fragment fragment = new RecievedFragment();
                    Bundle bundl = new Bundle();
                    bundl.putString("key", value);
                    fragment.setArguments(bundl);

                    fragmentTransaction.replace(R.id.frame_layout, fragment);
                    fragmentTransaction.commit();
                }
                }

        });

        return view;
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
                    Toast.makeText(getContext(), "OTP Has Expired", Toast.LENGTH_SHORT).show();
                }
            }
        }, delayUntilExpiry);
    }


}
