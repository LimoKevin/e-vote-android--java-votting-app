package com.company.evote;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomAlertDialog {
    private AlertDialog dialog;

    public CustomAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_dialog, null);

        TextView titleTextView = view.findViewById(R.id.dialog_title);
        titleTextView.setText(title);

        TextView messageTextView = view.findViewById(R.id.desc);
        messageTextView.setText(message);


        Button okButton = view.findViewById(R.id.btnreset);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        builder.setView(view);
        dialog = builder.create();
    }

    public void show() {
        dialog.show();
    }
}
