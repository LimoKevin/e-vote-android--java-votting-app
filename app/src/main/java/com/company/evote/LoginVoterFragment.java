package com.company.evote;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
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


public class LoginVoterFragment extends Fragment {
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

        View view = inflater.inflate(R.layout.fragment_login_voter, container, false);



        mAuth = FirebaseAuth.getInstance();

        forgotpass = view.findViewById(R.id.forgot);
        progressDialog = new ProgressDialog(getActivity());
        email = view.findViewById(R.id.Studentemail);
        pass = view.findViewById(R.id.pass);
        login = view.findViewById(R.id.btnsignin);
        login.setOnClickListener(v -> {
            String uemail = email.getText().toString().trim();
            String upass = pass.getText().toString().trim();

            if (!uemail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(uemail).matches()) {
                progressDialog.setTitle("Login");
                progressDialog.setMessage("Please Wait while Login...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);

                progressDialog.show();

                if (!upass.isEmpty()) {
                    DbHelper.loginUser(uemail, upass, new DbHelper.OnLoginResultListener() {
                        @Override
                        public void onSuccess() {
                            DbHelper.checkUserRole(new DbHelper.OnUserRoleResultListener() {
                                @Override
                                public void onUserRoleSuccess() {
                                    DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference("Dates").child("today");
                                    datesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String eventType = dataSnapshot.getValue(String.class);
                                                if ("results".equals(eventType)) {
                                                    startActivity(new Intent(getActivity(), ResultsActivity.class));
                                                    getActivity().finish();
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                                } else{
                                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                                    getActivity().finish();
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle error
                                        }
                                    });
                                }

                                @Override
                                public void onUserRoleAdmin() {
                                    Toast.makeText(getContext(), "You are not  voter. Please login as admin.", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onUserRoleNotDefined() {
                                    Toast.makeText(getActivity(), "User role not defined", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onUserRoleError(DatabaseError error) {
                                    // Handle database error
                                }
                            });
                        }

                        @Override
                        public void onEmailNotVerified() {
                            Toast.makeText(getContext(), "Please Check your mail.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        // ... existing code ...
                    });
                } else {
                    pass.setError("Password cannot be Empty.");
                }
            } else if (uemail.isEmpty()) {
                email.setError("Email cannot be empty");
            } else {
                email.setError("Enter a valid email");
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

        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailBox.getText().toString();

                if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    Toast.makeText(getActivity(), "Enter a valid email address",Toast.LENGTH_SHORT).show();
                    return;
                }

                DbHelper.sendPasswordResetEmail(userEmail, new DbHelper.OnSendResetEmailResultListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getActivity(), "Check your email for password reset instructions", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getActivity(), "Failed to send reset email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
