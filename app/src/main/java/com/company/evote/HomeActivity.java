package com.company.evote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
       ,View.OnClickListener{
    DrawerLayout drawer;
    ImageView imageView,imageView3;


    SliderView sliderView;

    String [] date ={
            "Date | 22nd -23rd Dec",
            "Deadline 17 Dec",
            "Deadline 20th Dec"
    };
    String [] desc ={
            "Time To Choose Our Leaders",
            "Apply now to be a leader now,",
            "Don't be left out, apply to vote."
    };
    String [] title1 ={
            "Laikipia Voting day 2023",
            "Candidate Application Ongoing",
            "Voter application in process"
    };
    int [] images = {
            R.drawable.moving,
            R.drawable.smith,
            R.drawable.small,
    };



    Button pres,sci,bus,edu,hd;
    Button[] buttons;
    CardView cardView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        replaceFragment(new PresidencyFragement());
        displayUserProfile();
        cardViewAnimation();
        loadImageFromFirebaseStorage();

         pres = findViewById(R.id.presidency);
         sci = findViewById(R.id.science);
         bus = findViewById(R.id.business);
         hd = findViewById(R.id.hds);
         edu = findViewById(R.id.education);

//        Toast.makeText(this, eventType, Toast.LENGTH_SHORT).show();

        buttons = new Button[]{pres, sci, bus, hd, edu};

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        drawer = findViewById(R.id.drawer_layout);
        imageView = findViewById(R.id.imageView);
        imageView3 = findViewById(R.id.imageView3);

        imageView3.setOnClickListener(this::onClick);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });



        sliderView = findViewById(R.id.image_slider);
        SliderAdapter sliderAdapter = new SliderAdapter(images,title1,desc,date);

        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.startAutoCycle();


        Button presidency = findViewById(R.id.presidency);
        Button science = findViewById(R.id.science);
        Button business = findViewById(R.id.business);
        Button hds = findViewById(R.id.hds);
        Button education = findViewById(R.id.education);


        presidency.setOnClickListener(this);
        science.setOnClickListener(this);
        business.setOnClickListener(this);
        hds.setOnClickListener(this);
        education.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
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
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.home:
                changeActivity(ResultsActivity.class);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.presidency:
                changeButtonColors(pres);
                replaceFragment(new PresidencyFragement());
                break;

            case R.id.science:
                replaceFragment(new ScienceFragment());
                changeButtonColors(sci);
                break;

            case R.id.business:
                replaceFragment(new BusinessFragment());
                changeButtonColors(bus);
                break;

            case R.id.hds:
                changeButtonColors(hd);
                replaceFragment(new HdsFragment());
                break;
            case R.id.education:
                changeButtonColors(edu);
                replaceFragment(new EducationFragment());
                break;
            case R.id.imageView3:
                changeActivity(AboutActivity.class);
        }

    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
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

                        nameTextView.setText(userName);// line 228
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
    ///animation

    private void cardViewAnimation() {
        final CardView cardView = findViewById(R.id.cardView3);

        // Set initial scale and alpha values
        cardView.setScaleX(0.5f);
        cardView.setScaleY(0.5f);
        cardView.setAlpha(0f);

        cardView.setVisibility(View.VISIBLE);

        // Create a scale animation for both X and Y axes
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(cardView, View.SCALE_X, 1f);
        scaleXAnimator.setDuration(1000);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(cardView, View.SCALE_Y, 1f);
        scaleYAnimator.setDuration(1000);
        // Create an alpha animation
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(cardView, View.ALPHA, 1f);
        alphaAnimator.setDuration(1000);
        // Create an AnimatorSet to run the animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);

        // Set an interpolator for a smooth animation curve (optional)
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
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