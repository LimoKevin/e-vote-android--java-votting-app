package com.company.evote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class PersonoInfoFragment extends Fragment {

    String [] items = {"Science and applied technology", "Hds", "Education", "Business"};
    String [] gender = {"Male", "Female", "Other"};
    String [] year = {"Year 1", "Year 2", "Year 3","Year 4"};

    ///form
    TextView error;
    EditText person_name, person_email, person_phone, person_regno,
            person_age,person_school, person_gender, person_year;
    Button save;

    AutoCompleteTextView autoCompleteTextView,autoCompleteGender, autoCompleteYear;
    ArrayAdapter<String> adapterItems,adapterGender,adapterYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_persono_info, container, false);


        //form data
        person_name = view.findViewById(R.id.full_name);
        person_email = view.findViewById(R.id.person_email);
        person_regno =view.findViewById(R.id.person_regno);
        person_age = view.findViewById(R.id.person_age);
        person_phone = view.findViewById(R.id.person_phone);
        person_school = view.findViewById(R.id.auto_text_school);
        person_gender = view.findViewById(R.id.auto_text_gender);
        person_year = view.findViewById(R.id.auto_text_year);

        error = view.findViewById(R.id.tv_error);

        save = view.findViewById(R.id.save);


        autoCompleteTextView = view.findViewById(R.id.auto_text_school);
        autoCompleteGender = view.findViewById(R.id.auto_text_gender);
        autoCompleteYear = view.findViewById(R.id.auto_text_year);

        adapterGender = new ArrayAdapter<String>(getActivity(), R.layout.list_items, gender);
        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.list_items, items);
        adapterYear = new ArrayAdapter<String>(getActivity(), R.layout.list_items, year);

        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteGender.setAdapter(adapterGender);
        autoCompleteYear.setAdapter(adapterYear);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String item = parent.getItemAtPosition(position).toString();
        }
    });


        save.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirm");
            builder.setMessage("Do you want to save changes.");

            // Positive button (e.g., "Yes")
            builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Redirect to another activity when the "Yes" button is clicked

                    String p_name = person_name.getText().toString().trim();
                    String p_email = person_email.getText().toString().trim();
                    String p_regno = person_regno.getText().toString().trim();
                    String p_school = person_school.getText().toString().trim();
                    String p_year = person_year.getText().toString().trim();
                    String p_gender = person_gender.getText().toString().trim();
                    String p_phone = person_phone.getText().toString().trim();
                    String p_age = person_age.getText().toString().trim();

                    //save the details to database
                    Person person = new Person(p_name,p_email,p_regno,p_phone,p_age,p_gender,p_school,p_year);
                    FirebaseDatabase.getInstance().getReference("personalInfo").
                            child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(person);

                    Toast.makeText(getActivity(), "successful", Toast.LENGTH_SHORT).show();
                    Intent me = new Intent(getActivity(), VerificationActivity.class);
                    me.putExtra("OPT", 1);
                    startActivity(me);
                }
            });

            // Negative button (e.g., "No")
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Dismiss the dialog when the "No" button is clicked
                    dialogInterface.dismiss();
                }
            });

            // Create and show the AlertDialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            }
        });

        return view;
    }
}