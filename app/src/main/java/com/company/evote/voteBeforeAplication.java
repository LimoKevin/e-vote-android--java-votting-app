package com.company.evote;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class voteBeforeAplication extends Fragment {

    Button apply;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.vote_before_application, container, false);

        apply = view.findViewById(R.id.apply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an instance of ApplicationFragment
                ApplicationFragment applicationFragment = new ApplicationFragment();

                // Replace the current fragment with ApplicationFragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, applicationFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return view;
    }
}