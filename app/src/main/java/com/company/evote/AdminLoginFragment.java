package com.company.evote;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginFragment extends Fragment {
    public TextView forgotpass;
    TextView signup;
    Button login;
    ProgressDialog progressDialog;
    EditText email, pass;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        forgotpass = view.findViewById(R.id.forgot);
        progressDialog = new ProgressDialog(getActivity());
        email = view.findViewById(R.id.Studentemail);
        pass = view.findViewById(R.id.pass);
        login = view.findViewById(R.id.btnsignin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uemail = email.getText().toString().trim();
                String upass = pass.getText().toString().trim();

                if (!uemail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
                    progressDialog.setTitle("Login");
                    progressDialog.setMessage("Please Wait while Login...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);

                    progressDialog.show();

                    if (!upass.isEmpty()) {
                        mAuth.signInWithEmailAndPassword(uemail, upass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
//                                progressDialog.dismiss();

                                if(mAuth.getCurrentUser().isEmailVerified()){
//                                    Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                    checkUserRole();
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "we have sent you a verification link", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        pass.setError("Password cannot be Empty.");
                    }
                } else if (uemail.isEmpty()) {
                    email.setError("Email cannot be empty");
                } else {
                    email.setError("Enter a valid email");
                }
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
        return view;
    }
    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_forgot, null);

        // Find the buttons and text views inside dialogView
        Button negativeButton = dialogView.findViewById(R.id.btncancel);
        Button positiveButton = dialogView.findViewById(R.id.btnreset);

        EditText emailBox = dialogView.findViewById(R.id.dialog_name);


        // Set the view for the dialog
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailBox.getText().toString();

                if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    Toast.makeText(getActivity(), "Enter a valid email address",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), "Check your mail",Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getActivity(), "Unable to send, failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // Show the dialog
        dialog.setCancelable(true);
        dialog.show();
    }

    private void checkUserRole() {
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference userRolesRef = FirebaseDatabase.getInstance().getReference().child("UserRoles").child(user.getUid());
        userRolesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.getValue(String.class);
                    if ("Admin".equals(role)) {
                        // User is an admin, redirect to the admin activity
                        Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), AdminHomeActivity.class));
                        getActivity().finish(); // Close the login activity
                    } else {
//                        // User is not an admin, redirect to the regular user activity
//                        startActivity(new Intent(getActivity(), HomeActivity.class));
//                        getActivity().finish(); // Close the login activity
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "You don't have an admin account.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the user's role is not defined
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "User role not defined", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}


