package com.company.evote;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Top4VotingFragment extends Fragment implements RecyclerViewInterface {

    // Define RecyclerView, Adapter, and List<Result>
    private RecyclerView recyclerView;
    private Adapter adapter;
    ArrayList<User> list;
    DatabaseReference databaseReference;


    int []arr = {R.drawable.img4, R.drawable.img9, R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img6,
            R.drawable.img7, R.drawable.img8};

    FrameLayout frameLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top4_voting, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Applicants");


        // Initialize RecyclerView, Adapter, and resultList
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        list = new ArrayList<>();

        adapter = new Adapter(this, list, arr);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        Query statusQuery = databaseReference.orderByChild("status").equalTo("accepted");
        Query schoolQuery = databaseReference.orderByChild("post").equalTo("President");

        statusQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot statusSnapshot) {
                schoolQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot schoolSnapshot) {
                        list.clear();

                        for (DataSnapshot statusData : statusSnapshot.getChildren()) {
                            for (DataSnapshot schoolData : schoolSnapshot.getChildren()) {
                                if (statusData.getKey().equals(schoolData.getKey())) {
                                    User user = statusData.getValue(User.class);

                                    if (user != null) {
                                        list.add(user);

                                    }
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
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
        return view;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onItemClick(int position) {
        User selectedUser = list.get(position);
        String selectedEmail = selectedUser.getEmail();
        Log.d("TAG", "This is an error message"+ selectedEmail);
        Toast.makeText(getContext(), "error  "+ selectedEmail, Toast.LENGTH_SHORT).show();

        // Start the new activity and pass the selected item's name
        Intent intent = new Intent(getContext(), ApprovalActivity.class);
        intent.putExtra("EMAIL", selectedEmail);
        startActivity(intent);
    }

    @Override
    public void onButtonClick(int position) {
        User selectedUser = list.get(position);
       String selected = String.valueOf(position);
        Toast.makeText(getContext(), "error  "+ selected, Toast.LENGTH_SHORT).show();


        String selectedEmail = selectedUser.getEmail();
        Toast.makeText(getContext(), "error  "+ selectedEmail, Toast.LENGTH_SHORT).show();
        Log.d("TAG", "This is an error message"+ selectedEmail);


        Intent intent = new Intent(getContext(), ApprovalActivity.class);
        intent.putExtra("EMAIL", selectedEmail);
        startActivity(intent);
    }
}