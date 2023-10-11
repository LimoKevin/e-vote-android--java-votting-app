package com.company.evote;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DbHelper {

    public static void saveUserProfileToDatabase(String username, String userEmail) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userProfilesRef = FirebaseDatabase.getInstance().getReference("UserProfiles")
                .child(currentUserId);
        User userProfile = new User(username, userEmail, false, "pending");
        userProfilesRef.setValue(userProfile);
    }

    public static void createVote() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference voteTable = FirebaseDatabase.getInstance().getReference().child("Voting").child(user.getUid());

        User voter = new User( false);
        voteTable.setValue(voter);
    }

    public static void setUserRoleInDatabase(String userId, String userRole) {
        DatabaseReference userRolesRef = FirebaseDatabase.getInstance().getReference("UserRoles")
                .child(userId);
        userRolesRef.setValue(userRole);
    }

    ////loging in the user
    public static void loginUser(String email, String password, OnLoginResultListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        listener.onSuccess();
                    } else {
                        listener.onEmailNotVerified();
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onFailure(e);
                });
    }

    public static void sendPasswordResetEmail(String email, OnSendResetEmailResultListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    public interface OnLoginResultListener {
        void onSuccess();

        void onEmailNotVerified();

        void onFailure(Exception e);
    }

    public interface OnSendResetEmailResultListener {
        void onSuccess();

        void onFailure(Exception e);
    }

    public static void checkUserRole(OnUserRoleResultListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference userRolesRef = FirebaseDatabase.getInstance().getReference().child("UserRoles").child(user.getUid());
        userRolesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.getValue(String.class);
                    if (!"Admin".equals(role)) {
                        listener.onUserRoleSuccess();
                    } else {
                        listener.onUserRoleAdmin();
                    }
                } else {
                    listener.onUserRoleNotDefined();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onUserRoleError(error);
            }
        });
    }


    public interface OnUserRoleResultListener {
        void onUserRoleSuccess();

        void onUserRoleAdmin();

        void onUserRoleNotDefined();

        void onUserRoleError(DatabaseError error);
    }
    public static void checkSchool(final OnSchoolCheckedListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference userRolesRef = FirebaseDatabase.getInstance().getReference().child("personalInfo").child(user.getUid()).child("school");
        userRolesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String school = dataSnapshot.getValue(String.class);
                if (school != null) {
                    listener.onSchoolChecked(school);
                } else {
                    listener.onSchoolChecked("Unknown");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onSchoolChecked("Unknown");
            }
        });
    }

    public interface OnSchoolCheckedListener {
        void onSchoolChecked(String school);
    }
    ///checking verification

    public static void checkIfUserIsVerified( OnUserVerificationResultListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        DatabaseReference personalInfoRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("personalInfo")
                .child(userId);

        personalInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isVerified = dataSnapshot.exists();
                listener.onUserVerificationResult(isVerified);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error if needed
                listener.onUserVerificationResult(false);
            }
        });
    }

    public interface OnUserVerificationResultListener {
        void onUserVerificationResult(boolean isVerified);
    }

    public static void checkVotingStatus(String userId, final VotingStatusCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference voteRef = databaseRef.child("Voting").child(userId).child("vote");

        voteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String voteStatus = dataSnapshot.getValue(String.class);

                if (voteStatus != null && !voteStatus.isEmpty()) {
                    callback.onVoteStatusReceived("voted");
                } else {
                    callback.onVoteStatusReceived(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onVoteStatusReceived(null);
            }
        });
    }
    public interface VotingStatusCallback {
        void onVoteStatusReceived(String status);
    }

//check if at top 1234
    public static boolean isCurrentUserInTopPositions() {
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
                            // You can perform any action here if needed
                            // For example, you can set a flag or perform a callback
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
        return false; // Default return value if conditions are not met
    }
}