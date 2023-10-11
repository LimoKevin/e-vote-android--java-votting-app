package com.company.evote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ApplicantsActivity extends AppCompatActivity implements RecyclerViewInterface{

    private ImageView backBtn;
    private RecyclerView recyclerView;
    private AdminRecycler adapter;
//    private Adapter adapter;

    DatabaseReference databaseReference;
    ArrayList<User> list;
    int []arr = {R.drawable.img4, R.drawable.img9, R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img6,
            R.drawable.img7, R.drawable.img8};

    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aplicants);

        replaceFragment(new ShimmerFragment());
        frameLayout = findViewById(R.id.frame_layout);
        frameLayout.setVisibility(View.VISIBLE);

        backBtn = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.recycler_view);
        databaseReference = FirebaseDatabase.getInstance().getReference("Applicants");

       backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(AdminHomeActivity.class);
            }
        });

        // Initialize the RecyclerView and its adapter here
        list = new ArrayList<>();

        // Initialize the RecyclerView and its adapter after loading data
        adapter = new AdminRecycler(this, list, arr,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                adapter.notifyDataSetChanged();
                frameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BottomNavigationView bottomNavigationView= findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    changeActivity(AdminHomeActivity.class);
                    break;
                case R.id.applicants:
                    break;
                case R.id.dates:
                    changeActivity(DatesActivity.class);
                    break;
                case R.id.profile:
                    changeActivity(ProfileActivity.class);
                    break;
            }

            return true;
        });
    }

    public void changeActivity(Class<?> nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        changeActivity(AdminHomeActivity.class);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(int position) {
        User selectedUser = list.get(position);
        String selectedName = selectedUser.getEmail();

        // Start the new activity and pass the selected item's name
        Intent intent = new Intent(getApplicationContext(), ApprovalActivity.class);
        intent.putExtra("EMAIL", selectedName);
        startActivity(intent);
    }

    @Override
    public void onButtonClick(int position) {

    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}

