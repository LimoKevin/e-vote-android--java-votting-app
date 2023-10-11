package com.company.evote;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ScienceResultFragment extends Fragment {

    // Define RecyclerView, Adapter, and List<Result>
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private ArrayList<Result> resultList;
    FrameLayout frameLayout1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_science_result, container, false);

        replaceFragment(new UserShimmerFragment());
        frameLayout1 = view.findViewById(R.id.frame_layout1);

        // Initialize RecyclerView, Adapter, and resultList
        recyclerView = view.findViewById(R.id.recycler_candidates);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        resultList = new ArrayList<>();
        adapter = new ResultAdapter(getContext(), resultList);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("Results");
        resultsRef.orderByChild("position")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Result result = snapshot.getValue(Result.class);
                            if (result != null && result.getPost().equals("Delegate of school") && result.getSchool().equals("Science and applied technology")) {
                                int position = result.getPosition();
                                resultList.add(result);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        frameLayout1.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
        return view;
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout1, fragment);
        fragmentTransaction.commit();
    }

}