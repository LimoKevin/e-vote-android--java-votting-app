package com.company.evote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminHomeActivity extends AppCompatActivity implements RecyclerViewInterface, Utils.PeriodCallback {
    private DrawerLayout drawer;
    private ImageView menu;
    private RecyclerView recyclerView;
    private AdminRecycler adapter;
    TextView seeAll, tvScience, tvBusiness, tvHds, tvTotal,
            tvEducation, tvAprroved, tvDeclined;

    DatabaseReference databaseReference;
    ArrayList<User> list;
    int []arr = {R.drawable.img4, R.drawable.img9, R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img6,
            R.drawable.img7, R.drawable.img8};

    FrameLayout frameLayout;
    ShimmerFrameLayout shimmerFrameLayout;

    LinearLayout gridLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        gridLinearLayout = findViewById(R.id.grid_linear_layout);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        tvBusiness = findViewById(R.id.tv_business);
        tvScience = findViewById(R.id.tv_science);
        tvEducation = findViewById(R.id.tv_education);
        tvHds = findViewById(R.id.tv_hds);
        tvAprroved = findViewById(R.id.tv_approved);
        tvDeclined = findViewById(R.id.tv_declined);
        tvTotal = findViewById(R.id.tv_total);


        getCountOfScience();
        getCountOfBusiness();
        getCountOfHds();
        getCountOfEducation();
        getCountOfDeclinedApplications();
        getCountOfapprovedApplications();
        getTotalApplicantsCount();

        replaceFragment(new ShimmerFragment());
        frameLayout = findViewById(R.id.frame_layout);
        frameLayout.setVisibility(View.VISIBLE);


        displayUserProfile();
        loadImageFromFirebaseStorage();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        seeAll = findViewById(R.id.see_all);
        drawer = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.menu_btn);
        recyclerView = findViewById(R.id.recycler_view);
        databaseReference = FirebaseDatabase.getInstance().getReference("Applicants");

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        // Initialize the RecyclerView and its adapter here
        list = new ArrayList<>();

        // Initialize the RecyclerView and its adapter after loading data
        adapter = new AdminRecycler(this, list, arr, this);
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
                recyclerView.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                linearLayoutAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity(ApplicantsActivity.class);
            }
        });

        BottomNavigationView bottomNavigationView= findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    break;
                case R.id.applicants:
                    changeActivity(ApplicantsActivity.class);
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            changeActivity(AdminHomeActivity.class);
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.home:
                changeActivity(HomeActivity.class);
                break;
            case R.id.applicants:
                changeActivity(ApplicantsActivity.class);;
                break;
            case R.id.dates:
                changeActivity(HomeActivity.class);
                break;
            case R.id.share:
                changeActivity(HomeActivity.class);
                break;
            case R.id.send:
                changeActivity(ProfileActivity.class);
                break;
        }
        return true;
    }

    public void changeActivity(Class<?> nextActivity) {
        Intent intent = new Intent(getApplicationContext(), nextActivity);
        startActivity(intent);
        finish();
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
                        String userEmail = dataSnapshot.child("email").getValue(String.class);

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

    ///animation

    private void linearLayoutAnimation() {
        final LinearLayout linearLayout = findViewById(R.id.grid_linear_layout);

        // Set initial scale and alpha values
        linearLayout.setScaleX(0.5f);
        linearLayout.setScaleY(0.5f);
        linearLayout.setAlpha(0f);

        linearLayout.setVisibility(View.VISIBLE);

        // Create a scale animation for both X and Y axes
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(linearLayout, View.SCALE_X, 1f);
        scaleXAnimator.setDuration(1000);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(linearLayout, View.SCALE_Y, 1f);
        scaleYAnimator.setDuration(1000);
        // Create an alpha animation
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(linearLayout, View.ALPHA, 1f);
        alphaAnimator.setDuration(1000);
        // Create an AnimatorSet to run the animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);

        // Set an interpolator for a smooth animation curve (optional)
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
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
    public void getCountOfScience() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("UserProfiles");
        Query accVerificationQuery = databaseRef.orderByChild("accverification").equalTo(true);

        accVerificationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String school = snapshot.child("school").getValue(String.class);
                    if ("Science and applied technology".equals(school)) {
                        count++;
                    }
                }
                String countString = String.valueOf(count);
                tvScience.setText(countString);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    public void getCountOfBusiness() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("UserProfiles");
        Query accVerificationQuery = databaseRef.orderByChild("accverification").equalTo(true);

        accVerificationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String school = snapshot.child("school").getValue(String.class);
                    if ("Business".equals(school)) {
                        count++;
                    }
                }
                String countString = String.valueOf(count);
                tvBusiness.setText(countString);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    public void getCountOfHds() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("UserProfiles");
        Query accVerificationQuery = databaseRef.orderByChild("accverification").equalTo(true);

        accVerificationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String school = snapshot.child("school").getValue(String.class);
                    if ("Hds".equals(school)) {
                        count++;
                    }
                }
                String countString = String.valueOf(count);
                tvHds.setText(countString);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    public void getCountOfEducation() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("UserProfiles");
        Query accVerificationQuery = databaseRef.orderByChild("accverification").equalTo(true);

        accVerificationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String school = snapshot.child("school").getValue(String.class);
                    if ("Education".equals(school)) {
                        count++;
                    }
                }
                String countString = String.valueOf(count);
                tvEducation.setText(countString);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    public void getCountOfDeclinedApplications() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Applicants");
        Query statusQuery = databaseRef.orderByChild("status").equalTo("declined");

        statusQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                String countString = String.valueOf(count);
                tvDeclined.setText(countString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    public void getCountOfapprovedApplications() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Applicants");
        Query statusQuery = databaseRef.orderByChild("status").equalTo("accepted");

        statusQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                String countString = String.valueOf(count);
                tvAprroved.setText(countString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    public void getTotalApplicantsCount() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Applicants");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                String countString = String.valueOf(count);
                tvTotal.setText(countString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    @Override
    public void onPeriodRetrieved(String period) {
        // Use the retrieved period here
        switch (period) {
            case "appl":
                // Handle application period
                Toast.makeText(this, "application ongoing", Toast.LENGTH_SHORT).show();
                break;
            case "voting":
                // Handle voting period
                Toast.makeText(this, "voting ongoing", Toast.LENGTH_SHORT).show();
                break;
            case "results":
                // Handle results period
                Toast.makeText(this, "voting is done please wait for results", Toast.LENGTH_SHORT).show();
                break;
            default:
                // Handle default case (if none of the above)
                break;
        }
    }
    private void loadImageFromFirebaseStorage() {
        String imageName = (FirebaseAuth.getInstance().getCurrentUser().getUid())+ ".jpg";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + imageName);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use an image loading library (e.g., Picasso, Glide) to load the image into the ImageView

                ImageView profileImage = findViewById(R.id.profileImage);
                Picasso.get().load(uri).into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors that may occur
                Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}