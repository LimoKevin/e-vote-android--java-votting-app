package com.company.evote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextView signup, tv_voter, tv_admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       signup = findViewById(R.id.signup);
       tv_voter = findViewById(R.id.tv_voter);
       tv_admin = findViewById(R.id.tv_admin);


       signup.setOnClickListener(this::onClick);
       tv_voter.setOnClickListener(this::onClick);
       tv_admin.setOnClickListener(this::onClick);

       replaceFragment(new LoginVoterFragment());

    }

    @Override
    public void onClick(View v) {
       LinearLayout lin_voter = findViewById(R.id.lin_voter);
       LinearLayout lin_admin = findViewById(R.id.lin_admin);

       switch (v.getId()){
           case R.id.signup:
               Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
               startActivity(intent);
               break;
           case R.id.tv_voter:
               tv_voter.setTextColor(getResources().getColor(R.color.primary));
               tv_admin.setTextColor(getResources().getColor(R.color.default_text_color));
               lin_voter.setVisibility(View.VISIBLE);
               lin_admin.setVisibility(View.GONE);
               replaceFragment(new LoginVoterFragment());
               break;
           case R.id.tv_admin:
               tv_admin.setTextColor(getResources().getColor(R.color.primary));
               tv_voter.setTextColor(getResources().getColor(R.color.default_text_color));
               lin_admin.setVisibility(View.VISIBLE);
               lin_voter.setVisibility(View.GONE);
               replaceFragment(new AdminLoginFragment());
               break;

       }

    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}