package com.company.evote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class VoteActivity extends AppCompatActivity implements RecyclerViewInterface{
    Fragment fragmentApply;

    ImageView voteback;
    Button apply, home;
    TextView schoolTv, votemore;
    String schoolName;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("personalInfo");

    RecyclerView recyclerView, recyclerView1;
    Adapter adapter;
    FrameLayout frameLayout, frameLayoutVoted;

    DatabaseReference databaseReference;
    ArrayList<User> list;
    private boolean isSafeToPerformTransaction;


    int []arr = { R.drawable.img3, R.drawable.img5, R.drawable.img6, R.drawable.img6,
            R.drawable.img7, R.drawable.img8};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        checkSchool();
        replaceFragment(new UserShimmerFragment());
//        replaceFragment1(new VoteFragmentAfter());

        schoolTv = findViewById(R.id.school);
        frameLayout = findViewById(R.id.frame_layout);
        frameLayoutVoted = findViewById(R.id.frame_layout_voted);

        schoolName = schoolTv.getText().toString();

//        recyclerView1 = findViewById(R.id.recycler_view1);
        recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference("Applicants");
        list = new ArrayList<>();
        adapter = new Adapter(this, list, arr);
        recyclerView.setAdapter(adapter);


        voteback = findViewById(R.id.voteback);
        voteback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        votemore = findViewById(R.id.votemore);
        votemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten = new Intent(getApplicationContext(), appActivity.class);
                startActivity(inten);
            }
        });

        isSafeToPerformTransaction = true;
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
                    break;
                case R.id.profile:
                    changeActivity(ProfileActivity.class);
                    break;
                case R.id.help:
                    changeActivity(HomeActivity.class);
                    break;
            }

            return true;
        });

    }
    public void changeActivity(Class<?> nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        startActivity(intent);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

    private void replaceFragment1(Fragment fragment) {
        if (isSafeToPerformTransaction) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout_voted, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(int position) {

    }

    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
        builder.setTitle("Verification");
        builder.setMessage("You have not verified you account.");

        // Positive button (e.g., "Yes")
        builder.setPositiveButton("settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Redirect to another activity when the "Yes" button is clicked
                Intent intent = new Intent(VoteActivity.this, VerificationActivity.class);
                startActivity(intent);
            }
        });

        // Negative button (e.g., "No")
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Dismiss the dialog when the "No" button is clicked
                dialogInterface.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialogVoted() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
        builder.setTitle("Vote");
        builder.setMessage("You have already voted.");

        // Positive button (e.g., "Yes")
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Redirect to another activity when the "Yes" button is clicked
                Intent intent = new Intent(VoteActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Negative button (e.g., "No")
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Dismiss the dialog when the "No" button is clicked
                dialogInterface.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onButtonClick(int position) {
        DbHelper.checkIfUserIsVerified(isVerified -> {
            if (isVerified) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DbHelper.checkVotingStatus(userId, new DbHelper.VotingStatusCallback() {
                    @Override
                    public void onVoteStatusReceived(String status) {
                        if (status == null) {
                            // User has voted
                            // The user is verified (the node exists)
                            User selectedUser = list.get(position);
                            String selectedName = selectedUser.getEmail();

                            // Start the new activity and pass the selected item's name
                            Intent intent = new Intent(getApplicationContext(), ApprovalActivity.class);
                            intent.putExtra("EMAIL", selectedName);
                            startActivity(intent);
                        } else {
                            showDialogVoted();
                        }
                    }
                });

            } else {
                // The user is not verified (the node doesn't exist)
                showDialogBox();
            }
        });
    }


    private void checkSchool() {
        DbHelper.checkSchool(new DbHelper.OnSchoolCheckedListener() {
            @Override
            public void onSchoolChecked(String school) {
                if (school != null) {
                    schoolTv.setText(school);
//                    Toast.makeText(VoteActivity.this, school, Toast.LENGTH_SHORT).show();
                    Query statusQuery = databaseReference.orderByChild("status").equalTo("accepted");
                    Query schoolQuery = databaseReference.orderByChild("school").equalTo(school);

                    statusQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot statusSnapshot) {
                            schoolQuery.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot schoolSnapshot) {
                                    list.clear();

                                    for (DataSnapshot statusData : statusSnapshot.getChildren()) {
                                        DataSnapshot schoolData = schoolSnapshot.child(statusData.getKey());
                                        if (schoolData.exists()) {
                                            User user = statusData.getValue(User.class);
                                            if (user != null && !user.getPost().equals("President")) {
                                                list.add(user);
                                            }
                                        }
                                    }

                                    adapter.notifyDataSetChanged();
                                    frameLayout.setVisibility(View.GONE);
                                    schoolTv.setText(school);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    checkIfDelegateTopFour();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Firebase", "Error reading school data: " + error.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Error reading status data: " + error.getMessage());
                        }
                    });
                } else {
                    // Handle the case where school data couldn't be retrieved
                    System.out.println("Failed to retrieve school information.");
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    }

    private void checkIfDelegateTopFour(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DbHelper.checkVotingStatus(userId, new DbHelper.VotingStatusCallback() {
            @Override
            public void onVoteStatusReceived(String status) {
                if (status == null) {
//                    schoolTv.setText("cool");
                    votemore.setVisibility(View.GONE);
                    frameLayout.setVisibility(View.GONE);
                    frameLayoutVoted.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
//                    replaceFragment1(new VoteFragmentAfter());
                } else {
                    boolean isCurrentUserInTopPositions = DbHelper.isCurrentUserInTopPositions();
                    if (isCurrentUserInTopPositions) {
//                        Toast.makeText(VoteActivity.this, " user In top positions", Toast.LENGTH_SHORT).show();
                    } else {
                        ///check if the user is a deligate and is top4
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("Results").child(userId);

                            resultsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Integer position = dataSnapshot.child("position").getValue(Integer.class);
                                        if (position != null && position >= 1 && position <= 4) {
                                            // User is in top positions (1, 2, 3, 4)
                                            recyclerView.setVisibility(View.GONE);
                                            frameLayoutVoted.setVisibility(View.VISIBLE);
                                            schoolTv.setText("Presidents");
                                            votemore.setVisibility(View.GONE);
                                            replaceFragment1(new Top4VotingFragment());
//                                            Toast.makeText(VoteActivity.this, "User in Top 4", Toast.LENGTH_SHORT).show();
                                        }else{
                                            votemore.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.GONE);
                                            frameLayoutVoted.setVisibility(View.VISIBLE);
                                            replaceFragment1(new VotedStatusFragment());
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle error
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isSafeToPerformTransaction = false;
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        isSafeToPerformTransaction = true;
    }
}