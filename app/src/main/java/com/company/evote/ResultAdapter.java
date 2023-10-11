package com.company.evote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {
    private ArrayList<Result> resultList;
    private Context context;
    int []arr = {R.drawable.img4, R.drawable.img9, R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img6,
            R.drawable.img7, R.drawable.img8};

    public ResultAdapter(Context context, ArrayList<Result> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_recycler_single, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Result result = resultList.get(position);
        // Bind data to ViewHolder views
        holder.imageView.setImageResource(arr[position]);
        holder.nameTextView.setText(result.getName());

        if (result.getPosition() == 1){
            holder.positionTextView.setText("winner");
            holder.linear.setBackgroundResource(R.drawable.winner_bg);
        }else{
            holder.positionTextView.setText("pos "+ String.valueOf(result.getPosition()));
            holder.linear.setBackgroundResource(R.drawable.normal_bg);
        }

// For getting the images from the database
        final String[] userId = new String[1];
        String emailToFind = result.getEmail();
        FirebaseDatabase.getInstance().getReference("UserProfiles")
                .orderByChild("email")
                .equalTo(emailToFind)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                userId[0] = userSnapshot.getKey();
                                String imagePath = "images/" + userId[0] + ".jpg";

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
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, schoolTextView, postTextView,positionTextView;
        CardView cardView;
        ImageView imageView;
        LinearLayout linear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_name);
            positionTextView = itemView.findViewById(R.id.tv_position);
            linear = itemView.findViewById(R.id.item_bg);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}

