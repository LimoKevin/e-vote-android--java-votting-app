package com.company.evote;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EducationFragment extends Fragment implements RecyclerViewInterface {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    DatabaseReference databaseReference;
    ArrayList<User> list;

    int []arr = { R.drawable.img3, R.drawable.img5, R.drawable.img6, R.drawable.img6,
            R.drawable.img7, R.drawable.img8};

    FrameLayout frameLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_education, container, false);


        replaceFragment(new UserShimmerFragment());
        frameLayout = view.findViewById(R.id.frame_layout1);

        recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference("Applicants");
        list = new ArrayList<>();

        ConstraintLayout noData = view.findViewById(R.id.no_data);

        if (list.isEmpty()) {
            noData.setVisibility(View.GONE);
        }else{
            noData.setVisibility(View.GONE);
        }

        // Initialize your adapter and set it to the RecyclerView
        adapter = new Adapter(this, list, arr);
        recyclerView.setAdapter(adapter);

        Query statusQuery = databaseReference.orderByChild("status").equalTo("accepted");
        Query schoolQuery = databaseReference.orderByChild("school").equalTo("Education");
        Query postQuery = databaseReference.orderByChild("post").equalTo("Delegate of school");

        statusQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot statusSnapshot) {
                schoolQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot schoolSnapshot) {
                        postQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                list.clear();

                                for (DataSnapshot statusData : statusSnapshot.getChildren()) {
                                    for (DataSnapshot schoolData : schoolSnapshot.getChildren()) {
                                        for (DataSnapshot postData : postSnapshot.getChildren()) {
                                            if (statusData.getKey().equals(schoolData.getKey()) && statusData.getKey().equals(postData.getKey())) {
                                                User user = statusData.getValue(User.class);

                                                if (user != null) {
                                                    list.add(user);
                                                }
                                            }
                                        }
                                    }
                                }

                                adapter.notifyDataSetChanged();
                                frameLayout.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Error reading post data: " + error.getMessage());
                            }
                        });
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
        Intent i = new Intent(getActivity(), VoteActivity.class);
        startActivity(i);
    }

    @Override
    public void onButtonClick(int position) {
        Intent i = new Intent(getActivity(),VoteActivity.class);
        startActivity(i);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout1, fragment);
        fragmentTransaction.commit();
    }
}