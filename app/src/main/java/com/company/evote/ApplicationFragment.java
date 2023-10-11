package com.company.evote;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ApplicationFragment extends Fragment implements View.OnClickListener {
    String [] items = {"Science and applied technology", "Hds", "Education", "Business", "TVET"};
    String [] gender = {"Male", "Female", "Other"};
    String [] year = {"Year 1", "Year 2", "Year 3","Year 4"};
    String [] post = {"President", "Delegate of school", "Speaker"};


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    TextView regNo, email,age;

    EditText phone,name;
    Button submit;
    ImageView imageView5;

    AutoCompleteTextView autoCompleteTextView,autoCompleteGender, autoCompleteYear, autoCompletePost, Pdf;
    ArrayAdapter<String> adapterItems,adapterGender,adapterYear,adapterPost;
    Intent data;

    private Uri selectedPdfUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_aplication, container, false);


        autoCompleteTextView = view.findViewById(R.id.auto_school);
        autoCompleteGender = view.findViewById(R.id.auto_complete_gender);
        autoCompleteYear = view.findViewById(R.id.auto_complete_year);
        autoCompletePost = view.findViewById(R.id.auto_complete_post);
//        Pdf = view.findViewById(R.id.edt_pdf);
        submit = view.findViewById(R.id.btn_submit);
        regNo = view.findViewById(R.id.regN0);
        email = view.findViewById(R.id.p_email);
        phone = view.findViewById(R.id.phone);
        name = view.findViewById(R.id.p_name);
        age = view.findViewById(R.id.p_age);

        imageView5 = view.findViewById(R.id.imageView5);


        adapterGender = new ArrayAdapter<String>(getActivity(), R.layout.list_items, gender);
        adapterItems = new ArrayAdapter<String>(getActivity(), R.layout.list_items, items);
        adapterYear = new ArrayAdapter<String>(getActivity(), R.layout.list_items, year);
        adapterPost = new ArrayAdapter<String>(getActivity(), R.layout.list_items, post);


        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteGender.setAdapter(adapterGender);
        autoCompleteYear.setAdapter(adapterYear);
        autoCompletePost.setAdapter(adapterPost);

//        Pdf.setOnClickListener(this::onClick);
        submit.setOnClickListener(this::onClick);
        imageView5.setOnClickListener(this::onClick);


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });
        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.edt_pdf:
////                selectPdf();
//                break;
            case R.id.btn_submit:
                dataUpload();
                break;
            case R.id.imageView5:
                Intent i = new Intent(getActivity(), VoteActivity.class);
                startActivity(i);
                break;
        }

    }

    private void dataUpload() {
        String a_name = name.getText().toString().trim();
        String a_email = email.getText().toString().trim();
        String a_regNo = regNo.getText().toString().trim();
        String a_phone = phone.getText().toString().trim();
        String a_age = age.getText().toString().trim();
        String a_school = autoCompleteTextView.getText().toString().trim();
        String a_year = autoCompleteYear.getText().toString().trim();
        String a_post = autoCompletePost.getText().toString().trim();
        String a_gender = autoCompleteGender.getText().toString().trim();
        String status = "Pending";

        if (a_name.isEmpty() || a_email.isEmpty() || a_age.isEmpty() || a_regNo.isEmpty() || a_phone.isEmpty()
                || a_age.isEmpty() || a_school.isEmpty() || a_year.isEmpty() || a_post.isEmpty() || a_gender.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            Applicants applicants = new Applicants(a_name, a_email, a_phone, a_gender, a_age, a_school, a_year, a_post, a_regNo, status);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                FirebaseDatabase.getInstance().getReference("Applicants").child(uid).setValue(applicants)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getActivity(), VoteActivity.class);
                                startActivity(i);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getActivity(), "No user logged in", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private static final int PICK_PDF_REQUEST = 1;
//
//    private void selectPdf() {
//        Intent intent = new Intent();
//        intent.setType("application/pdf");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST);
//    }
//
//    private void uploadPdf() {
//        if (selectedPdfUri == null) {
//            Toast.makeText(getActivity(), "No PDF selected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String email = currentUser.getEmail();
//            if (email != null) {
//                String fileName = email + ".pdf";
//                StorageReference storageRef = storage.getReference().child("pdfs").child(fileName);
//
//                UploadTask uploadTask = storageRef.putFile(selectedPdfUri);
//                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(getActivity(), "PDF uploaded successfully", Toast.LENGTH_SHORT).show();
//
//                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                String downloadUrl = uri.toString();
//                                FirebaseDatabase.getInstance().getReference("Applicants").child(currentUser.getUid())
//                                        .child("pdfUrl").setValue(downloadUrl)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                // PDF URL saved to database
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Toast.makeText(getActivity(), "Error saving PDF URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                            }
//                        });
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getActivity(), "Error uploading PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//                Toast.makeText(getActivity(), "No user email found", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(getActivity(), "No user logged in", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            selectedPdfUri = data.getData();
//
//            String pdfName = getFileName(selectedPdfUri); // Get the name of the selected PDF
//            autoCompleteTextView.setText(pdfName); // Set the name in the AutoCompleteTextView
//            Toast.makeText(getActivity(), "PDF selected: " + pdfName, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private String getFileName(Uri uri) {
//        String displayName = null;
//        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            displayName = cursor.getString(nameIndex);
//        }
//        if (cursor != null) {
//            cursor.close();
//        }
//        return displayName;
//    }
}
