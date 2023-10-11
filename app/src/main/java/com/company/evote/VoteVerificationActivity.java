package com.company.evote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

public class VoteVerificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_verification);

        String passedValue = getIntent().getStringExtra("VOTE");

        Toast.makeText(getApplicationContext(), passedValue, Toast.LENGTH_SHORT).show();

        Fragment fragment = new SendOTPFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", passedValue);
        fragment.setArguments(bundle);

// Then replace the fragment in the frame layout
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();

//        replaceFragment(new SendOTPFragment());
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}