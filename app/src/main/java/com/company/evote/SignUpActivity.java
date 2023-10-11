package com.company.evote;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progressDialog;
    Button button;
    TextView signin;
    EditText name, email, pass, conpass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        signin = findViewById(R.id.signin);
        button = findViewById(R.id.button);
        name = findViewById(R.id.Name);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        conpass = findViewById(R.id.conpass);

        button.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signin:
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.button:
                final String uname = name.getText().toString().trim();
                final String uemail = email.getText().toString().trim();
                String upass = pass.getText().toString().trim();
                String uconpass = conpass.getText().toString().trim();
                if (!isConnected()) {
                    showNetworkErrorDialog();
                    return; // Don't proceed further
                }else{

                    if (uname.isEmpty()) {
                        name.setError("Username cannot be empty");
                        return;
                    } else if (uemail.isEmpty()) {
                        email.setError("Email cannot be empty");
                        return;
                    } else if (upass.isEmpty()) {
                        pass.setError("Please set a password");
                        return;
                    } else if (uconpass.isEmpty() || !upass.equals(uconpass)) {
                        pass.setError("Password does not match!");
                        conpass.setError("Password does not match!");
                        return;
                    }

                    progressDialog.setTitle("SignUp");
                    progressDialog.setMessage("Please Wait while Registering ...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(uemail, upass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();

                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "Sign-up successful. Please verify your email", Toast.LENGTH_SHORT).show();

                                            // Use DbHelper to perform database operations
                                            DbHelper.saveUserProfileToDatabase(uname, uemail);
                                            DbHelper.setUserRoleInDatabase(mAuth.getCurrentUser().getUid(), "User");
                                            DbHelper.createVote();

                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Exception exception = task.getException();
                                if (exception instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(SignUpActivity.this, "Account already exists. Please login.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Sign-up Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }

                break;
        }
    }
    private void showNetworkErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Network Error");
        builder.setMessage("Please check your internet connection.");

        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}




