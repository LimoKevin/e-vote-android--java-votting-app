package com.company.evote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.nio.channels.ClosedByInterruptException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DocsFragment extends Fragment {
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PDF_FILE = 3;

    private Uri imageUri1;
    private Uri imageUri2;
    private Uri pdfUri;

    private FirebaseStorage storage;

    private ImageView add_front;
    private ImageView add_back;
    private TextView tv_pdf;

    ConstraintLayout front, back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_docs, container, false);

        storage = FirebaseStorage.getInstance();

        add_front = view.findViewById(R.id.img_add_front);
        add_back = view.findViewById(R.id.img_add_back);
        tv_pdf = view.findViewById(R.id.tv_pdf);
        Button btn_submit = view.findViewById(R.id.btn_submit);

        front = view.findViewById(R.id.Layout_Front);
        back = view.findViewById(R.id.Layout_back);

        add_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent(1);
                } else {
                    requestCameraPermission();
                }
            }
        });

        add_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent(2);
                } else {
                    requestCameraPermission();
                }
            }
        });

        tv_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPdfFile();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null && imageUri2 != null) {
                    saveImagesToFirebaseStorage();
                } else {
                    Toast.makeText(requireContext(), "Please capture both images first", Toast.LENGTH_SHORT).show();
                }
                if (pdfUri != null) {
                    uploadPdfFile();
                } else {
                    Toast.makeText(requireContext(), "Please select a PDF file first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            Toast.makeText(requireContext(), "Camera permission is required to capture photos", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    private void dispatchTakePictureIntent(int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, requestCode);
        }
    }

    private void selectPdfFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_PDF_FILE);
    }

    private void uploadPdfFile() {
        StorageReference storageRef = storage.getReference();
        StorageReference pdfRef = storageRef.child("documents/document.pdf");

        UploadTask uploadTask = pdfRef.putFile(pdfUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(requireContext(), "PDF file uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase Storage", "PDF file upload failed: " + e.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PDF_FILE && resultCode == getActivity().RESULT_OK && data != null) {
            pdfUri = data.getData();
            // Display the selected PDF file name in a TextView or perform any desired action
            // For example, you can set the TextView text as:
            // tv_pdf.setText(getFileNameFromUri(pdfUri));
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK && data != null) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            if (requestCode == 1) {
                add_front.setImageBitmap(imageBitmap);
                imageUri1 = getImageUri(imageBitmap);
            } else if (requestCode == 2) {
                add_back.setImageBitmap(imageBitmap);
                imageUri2 = getImageUri(imageBitmap);
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, "Image", null);
        return Uri.parse(path);
    }

    private void saveImagesToFirebaseStorage() {
        StorageReference storageRef = storage.getReference();

        StorageReference imageRef1 = storageRef.child("images/image1.jpg");
        UploadTask uploadTask1 = imageRef1.putFile(imageUri1);
        uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(requireContext(), "Image 1 uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase Storage", "Image 1 upload failed: " + e.getMessage());
            }
        });

        StorageReference imageRef2 = storageRef.child("images/image2.jpg");
        UploadTask uploadTask2 = imageRef2.putFile(imageUri2);
        uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(requireContext(), "Image 2 uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firebase Storage", "Image 2 upload failed: " + e.getMessage());
            }
        });
    }
}

