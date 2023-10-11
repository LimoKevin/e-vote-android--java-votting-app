package com.company.evote;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.shimmer.ShimmerFrameLayout;

public class ShimmerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     View view = inflater.inflate(R.layout.fragment_shimmer, container, false);

        ShimmerFrameLayout shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        ShimmerFrameLayout shimmerFrameLayout2 = view. findViewById(R.id.shimmer_view_container2);
        ShimmerFrameLayout shimmerFrameLayout3 = view.findViewById(R.id.shimmer_view_container3);
        ShimmerFrameLayout shimmerFrameLayout4 = view.findViewById(R.id.shimmer_view_container4);
        ShimmerFrameLayout shimmerFrameLayout5 = view.findViewById(R.id.shimmer_view_container5);



        // Start Shimmer animation
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout2.startShimmer();
            shimmerFrameLayout3.startShimmer();
            shimmerFrameLayout4.startShimmer();
            shimmerFrameLayout5.startShimmer();

        return view;
    }
}