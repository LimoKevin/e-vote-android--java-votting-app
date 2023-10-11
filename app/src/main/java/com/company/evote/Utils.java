package com.company.evote;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils{
    //from email
    public static final String EMAIL = "limokevin30@gmail.com";
    public static final String PASSWORD = "lxjr gryj sptg iorz";

    public interface PeriodCallback {
        void onPeriodRetrieved(String period);
    }

    public static void timePeriod(PeriodCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference personalInfoRef = database.getReference().child("Dates");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        personalInfoRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentPeriod = null;
                if (dataSnapshot.exists()) {
                    String application = dataSnapshot.child("application").getValue(String.class);
                    String voting = dataSnapshot.child("voting").getValue(String.class);
                    String results = dataSnapshot.child("results").getValue(String.class);

                    String userCountry = Locale.getDefault().getCountry();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                    dateFormat.setTimeZone(TimeZone.getTimeZone(userCountry));

                    String currentDateInUserCountry = dateFormat.format(new Date());
                    if (currentDateInUserCountry.equals(application)){
                        currentPeriod = "appl";
                    }
                    if (currentDateInUserCountry.equals(voting)){
                        currentPeriod = "voting";
                    }
                    if (currentDateInUserCountry.equals(results)){
                        currentPeriod = "results";
                    }
                }

                callback.onPeriodRetrieved(currentPeriod);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if necessary
            }
        });
    }
}


