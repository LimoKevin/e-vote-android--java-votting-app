package com.company.evote;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VoteAfterDeligate extends Fragment implements RecyclerViewInterface{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    DatabaseReference databaseReference;
    ArrayList<User> list;

    int []arr = {R.drawable.img1, R.drawable.img2, R.drawable.img3,
            R.drawable.img4, R.drawable.img5, R.drawable.img6, R.drawable.img6,
            R.drawable.img7, R.drawable.img8, R.drawable.img9};

//    String [] year = {"3rd year", "1st year", "4th year", "2nd year","4th year",
//            "2nd year","1st year","2nd year", "4th year", "3rd year"};
//
//    String [] name = {"Kevin Kiprotich", "James Kamau", "Limo kevin", "John Doe",
//            "Wilfred Oluko", "Neville Oyuga", "Mary Anyango", "Cynthia Jane", "Juliana babygal", "Otieno Jayden"};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_deligate_after, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        list = new ArrayList<>();

        // Initialize your adapter and set it to the RecyclerView
        adapter = new Adapter(this, list, arr);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        Intent i = new Intent(getActivity(), VoteActivity.class);
        startActivity(i);
    }
}