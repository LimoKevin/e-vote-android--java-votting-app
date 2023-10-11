package com.company.evote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
        ,View.OnClickListener{
    DrawerLayout drawer;
    ImageView imageView,imageView3;

    // Define RecyclerView, Adapter, and List<Result>
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private ArrayList<Result> resultList;


    Button pres,sci,bus,edu,hd;
    Button[] buttons;
    FrameLayout frameLayout1;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        displayUserProfile();

        replaceFragment1(new UserShimmerFragment());
        frameLayout1 = findViewById(R.id.frame_layout1);
        frameLayout1.setVisibility(View.VISIBLE);
        frameLayout = findViewById(R.id.frame_layout);
        frameLayout.setVisibility(View.VISIBLE);

        replaceFragment(new ScienceResultFragment());

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        drawer = findViewById(R.id.drawer_layout);
        imageView = findViewById(R.id.imageView);
        imageView3 = findViewById(R.id.imageView3);

        imageView3.setOnClickListener(this::onClick);

        sci = findViewById(R.id.science);
        bus = findViewById(R.id.business);
        hd = findViewById(R.id.hds);
        edu = findViewById(R.id.education);

        buttons = new Button[]{sci, bus, hd, edu};

        Button science = findViewById(R.id.science);
        Button business = findViewById(R.id.business);
        Button hds = findViewById(R.id.hds);
        Button education = findViewById(R.id.education);


        science.setOnClickListener(this);
        business.setOnClickListener(this);
        hds.setOnClickListener(this);
        education.setOnClickListener(this);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
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
                    changeActivity(HelpActivity.class);
                    break;
            }

            return true;
        });
//        recyclerInitialization();
        presidentRecycler();


// For getting the user ids
        ArrayList<String> userIds = new ArrayList<>();

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Voting");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    userIds.add(userId);
                }

                processArrayList(userIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ResultsActivity.this, "Error loading user IDs", Toast.LENGTH_SHORT).show();
            }
        });

        deligatesRanking("Education");
        presidentRanking();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                changeActivity(HomeActivity.class);
                break;
            case R.id.message:
                changeActivity(HomeActivity.class);
                break;
            case R.id.notification:
                changeActivity(VoteActivity.class);
                break;
            case R.id.share:
                changeActivity(AdminHomeActivity.class);
                break;
            case R.id.send:
                changeActivity(HomeActivity.class);
                break;
        }
        return true;
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
    private void replaceFragment1(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout1,fragment);
        fragmentTransaction.commit();
    }
    private void displayUserProfile() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            DatabaseReference userProfileRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("UserProfiles")
                    .child(user.getUid());

            userProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        String userEmail = user.getEmail();

                        TextView nameTextView = findViewById(R.id.unameTv);
                        TextView emailTextView = findViewById(R.id.uemailTv);

                        nameTextView.setText(userName);
                        emailTextView.setText(userEmail);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database errors if necessary
                }
            });
        }
    }
    public void changeActivity(Class<?> nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.science:
                replaceFragment(new ScienceResultFragment());
                changeButtonColors(sci);
                break;

            case R.id.business:
                replaceFragment(new BusinessResultsFragment());
                changeButtonColors(bus);
                break;

            case R.id.hds:
                changeButtonColors(hd);
                replaceFragment(new HdsResultsFragment());
                break;
            case R.id.education:
                changeButtonColors(edu);
                replaceFragment(new EducationResultsFragment());
                break;
            case R.id.imageView3:
                changeActivity(AboutActivity.class);
        }

    }
    public void changeButtonColors(Button clickedButton) {
        for (Button button : buttons) {
            if (button.equals(clickedButton)) {
//                button.setBackgroundTintList(getResources().getColorStateList(android.R.color.white));
                button.setBackgroundTintList(this.getResources().getColorStateList(R.color.selected));
            } else {
                button.setBackgroundTintList(this.getResources().getColorStateList(R.color.primary));
            }
        }
    }

    public void processArrayList(ArrayList<String> userIds) {
        if (userIds != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Applicants");

            for (String userId : userIds) {
                DatabaseReference userRef = databaseReference.child(userId);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                String name = user.getName();
                                String school = user.getSchool();
                                String post = user.getPost();

                                DatabaseReference votingRef = FirebaseDatabase.getInstance().getReference("Voting");
                                votingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int count = 0;

                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            DataSnapshot voteSnapshot = userSnapshot.child("vote");

                                            if (voteSnapshot.exists()) {
                                                String voteValue = voteSnapshot.getValue(String.class);

                                                if (voteValue != null && voteValue.equals(userId)) {
                                                    count++;
                                                }
                                            }
                                        }
                                        int position = 1;
                                        saveDataToResults(userId, name, school, post, position, count);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(ResultsActivity.this, "Error loading vote data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ResultsActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(ResultsActivity.this, "User IDs list is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveDataToResults(String userId, String name, String school, String post, int position, int count) {
        DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("Results").child(userId);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("name", name);
        resultData.put("school", school);
        resultData.put("post", post);
        resultData.put("position", position);
        resultData.put("count", count);

        resultsRef.setValue(resultData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ResultsActivity.this, "Data saved to Results table", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResultsActivity.this, "Failed to save data to Results table", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //president ranking
    private void presidentRanking() {
        DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("Results");

        countRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<DataSnapshot> countNodes = new ArrayList<>();

                // Step 1: Retrieve all the "count" nodes where post equals "president"
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot postSnapshot = userSnapshot.child("post");

                    if (postSnapshot.exists() && postSnapshot.getValue(String.class).equals("president")) {
                        DataSnapshot countSnapshot = userSnapshot.child("count");
                        if (countSnapshot.exists()) {
                            countNodes.add(countSnapshot);
                        }
                    }
                }

                // Step 2: Compare values and determine rankings
                Collections.sort(countNodes, new Comparator<DataSnapshot>() {
                    @Override
                    public int compare(DataSnapshot dataSnapshot1, DataSnapshot dataSnapshot2) {
                        Integer count1 = dataSnapshot1.getValue(Integer.class);
                        Integer count2 = dataSnapshot2.getValue(Integer.class);

                        return count2.compareTo(count1);
                    }
                });

                int position = 1;
                for (DataSnapshot countSnapshot : countNodes) {
                    String userId = countSnapshot.getRef().getParent().getKey();

                    // Step 3: Update the "positions" node
                    DatabaseReference positionsRef = FirebaseDatabase.getInstance()
                            .getReference("Results")
                            .child(userId)
                            .child("position");

                    positionsRef.setValue(position);

                    position++;
                }

//                Toast.makeText(ResultsActivity.this, "Users ranked successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ResultsActivity.this, "Error ranking users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //president recycler
    public void presidentRecycler(){
        // Initialize RecyclerView, Adapter, and resultList
        recyclerView = findViewById(R.id.recycler_pres);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        resultList = new ArrayList<>();
        adapter = new ResultAdapter(this, resultList);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("Results");
        resultsRef.orderByChild("position")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Result result = snapshot.getValue(Result.class);//line 431
                            if (result != null && result.getPost().equals("President")) {
                                result.setPosition(snapshot.child("position").getValue(Integer.class)); // Retrieve position as Integer
                                resultList.add(result);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        frameLayout1.setVisibility(View.GONE);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });

    }
// school ranking
    private void deligatesRanking(String school) {
        DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("Results");

        countRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<DataSnapshot> countNodes = new ArrayList<>();

                // Step 1: Retrieve all the "count" nodes where school equals "Education" and post equals "Deligate"
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot schoolSnapshot = userSnapshot.child("school");
                    DataSnapshot postSnapshot = userSnapshot.child("post");

                    if (schoolSnapshot.exists() && schoolSnapshot.getValue(String.class).equals(school) &&
                            postSnapshot.exists() && postSnapshot.getValue(String.class).equals("Delegate of school")) {
                        DataSnapshot countSnapshot = userSnapshot.child("count");
                        if (countSnapshot.exists()) {
                            countNodes.add(countSnapshot);
                        }
                    }
                }

                // Step 2: Compare values and determine rankings
                Collections.sort(countNodes, new Comparator<DataSnapshot>() {
                    @Override
                    public int compare(DataSnapshot dataSnapshot1, DataSnapshot dataSnapshot2) {
                        Integer count1 = dataSnapshot1.getValue(Integer.class);
                        Integer count2 = dataSnapshot2.getValue(Integer.class);

                        return count2.compareTo(count1);
                    }
                });

                int position = 1;
                for (DataSnapshot countSnapshot : countNodes) {
                    String userId = countSnapshot.getRef().getParent().getKey();

                    // Step 3: Update the "positions" node
                    DatabaseReference positionsRef = FirebaseDatabase.getInstance()
                            .getReference("Results")
                            .child(userId)
                            .child("position");

                    positionsRef.setValue(position);

                    position++;
                }

//                Toast.makeText(ResultsActivity.this, "Users ranked successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ResultsActivity.this, "Error ranking users: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

