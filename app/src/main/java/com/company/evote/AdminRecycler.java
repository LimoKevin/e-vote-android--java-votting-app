


package com.company.evote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminRecycler extends RecyclerView.Adapter<AdminRecycler.ViewHolder> {
    private Context context;
    public static RecyclerViewInterface recyclerViewInterface;
    ArrayList<User> list;
    int[]arr;

    public AdminRecycler(RecyclerViewInterface recyclerViewInterface, ArrayList<User> list, int[] arr, Context context) {
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
        this.list = list;
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_single, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AdminRecycler.ViewHolder holder, int position) {
        User user = list.get(position);

        holder.textView.setText(user.getName());
        holder.year.setText(user.getYear());
        holder.deligateTv.setText(user.getPost());
        holder.decline.setText(user.getStatus());
        holder.schoolTv.setText(user.getSchool());

        switch (user.getStatus()){
            case "accepted":
                holder.decline.setTextColor(ContextCompat.getColor(context, R.color.acepted));
                break;
            case "pending":
                holder.decline.setTextColor(ContextCompat.getColor(context, R.color.pending));
                break;
            case "declined":
                holder.decline.setTextColor(ContextCompat.getColor(context, R.color.primary));
                break;
            default:
                holder.decline.setTextColor(ContextCompat.getColor(context, R.color.black));
                break;
        }

        final String[] userId = new String[1];
        String emailToFind = user.getEmail();
        FirebaseDatabase.getInstance().getReference("UserProfiles")
                .orderByChild("email")
                .equalTo(emailToFind)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                               userId[0] = userSnapshot.getKey();
                                Log.d("TAG", "Image name: " + userId[0]);
                                String imagePath = "images/" + userId[0] + ".jpg";

                                Log.d("TAG", "Image Path: " + imagePath);

                                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                StorageReference imageRef = storageReference.child(imagePath);

                                imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                        // Set the bitmap to the ImageView
                                        holder.imageView.setImageBitmap(bitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.e("TAG", "Failed to retrieve image: " + exception.getMessage());
                                    }
                                });

                            }
                        } else {
                            // No user found with the provided email
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error
                    }
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TextView year, schoolTv, deligateTv;
        public ImageView imageView;
        TextView decline;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ap_image);
            textView = itemView.findViewById(R.id.name);
            deligateTv = itemView.findViewById(R.id.tv_deligate);
            year = itemView.findViewById(R.id.year);
            decline = itemView.findViewById(R.id.decline_btn);
            schoolTv = itemView.findViewById(R.id.tv_school);

            String userId;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }

}
