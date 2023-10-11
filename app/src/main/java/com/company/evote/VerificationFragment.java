package com.company.evote;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class VerificationFragment extends Fragment implements View.OnClickListener{

    CardView cardViewInfo, cardViewPhone, cardViewFace, cardViewEmail, cardViewDocs;

    FirebaseAuth mAuth;
    ImageView image_email, statusIcon, statusIcon1, statusIcon2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verification, container, false);

        cardViewInfo = view.findViewById(R.id.cardViewInfo);
        cardViewFace = view.findViewById(R.id.cardViewFace);
        cardViewEmail = view.findViewById(R.id.cardViewEmail);
        cardViewFace = view.findViewById(R.id.cardViewFace);
        image_email = view.findViewById(R.id.img_email);

        statusIcon = view.findViewById(R.id.statusIcon);
        statusIcon2 = view.findViewById(R.id.statusIcon2);

        mAuth = FirebaseAuth.getInstance();

        cardViewInfo.setOnClickListener(this);
        cardViewPhone.setOnClickListener(this);
        cardViewFace.setOnClickListener(this);
        cardViewEmail.setOnClickListener(this);

        if (mAuth.getCurrentUser().isEmailVerified()){
            image_email.setImageResource(R.drawable.ic_baseline_check_24);
        }

        DbHelper.checkIfUserIsVerified(isVerified -> {
            if (isVerified) {
                // The user is verified (the node exists)
                statusIcon.setImageResource(R.drawable.ic_baseline_check_24);
                statusIcon2.setImageResource(R.drawable.ic_baseline_check_24);
            } else {
                // The user is not verified (the node doesn't exist)
            }
        });

        return view;
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cardViewInfo:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,((VerificationActivity)getActivity())
                        .fragmentInfo).addToBackStack(null).commit();
                break;
            case R.id.cardViewPhone:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,((VerificationActivity)getActivity())
                        .fragmentVer).addToBackStack(null).commit();
                break;
            case R.id.cardViewFace:
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,((VerificationActivity)getActivity())
//                        .fragmentFac).addToBackStack();null).commit(

//                Intent abc = new Intent(getActivity(), CameraActivity.class);
//                startActivity(abc);
                break;
            case R.id.cardViewEmail:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,((VerificationActivity)getActivity())
                        .fragmentVer).addToBackStack(null).commit();
                break;
        }
    }
}