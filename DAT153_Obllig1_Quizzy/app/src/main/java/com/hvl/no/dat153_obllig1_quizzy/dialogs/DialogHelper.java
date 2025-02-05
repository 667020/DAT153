package com.hvl.no.dat153_obllig1_quizzy.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.hvl.no.dat153_obllig1_quizzy.databinding.DialogAddEntryBinding;
import com.hvl.no.dat153_obllig1_quizzy.databinding.DialogNameEntryBinding;

public class DialogHelper {

    /**
     * Callback interface for the Add Entry dialog.
     */
    public interface OnAddEntryListener {
        void onCameraSelected();
        void onGallerySelected();
    }

    /**
     * Displays the Add Entry dialog.
     *
     * @param context  The Context used to inflate the dialog.
     * @param listener A callback to handle button selections.
     */
    public static void showAddEntryDialog(Context context, OnAddEntryListener listener) {
        // Inflate the add entry dialog layout using view binding.
        DialogAddEntryBinding binding = DialogAddEntryBinding.inflate(LayoutInflater.from(context));
        Dialog dialog = new Dialog(context);
        dialog.setContentView(binding.getRoot());

        // When "Add from Camera" is clicked, dismiss the dialog and call the callback.
        binding.btnAddFromCamera.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onCameraSelected();
            }
        });

        // When "Add from Gallery" is clicked, dismiss the dialog and call the callback.
        binding.btnAddFromGallery.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) {
                listener.onGallerySelected();
            }
        });

        // Finally, show the dialog.
        dialog.show();
    }

    /**
     * Callback interface for the Name Entry dialog.
     */
    public interface OnNameEnteredListener {
        void onNameEntered(String name);
    }

    /**
     * Displays the Name Entry dialog.
     *
     * @param context  The Context used to inflate the dialog.
     * @param listener A callback that returns the entered name.
     */
    public static void showNameDialog(Context context, OnNameEnteredListener listener) {
        // Inflate the name entry dialog layout using view binding.
        DialogNameEntryBinding binding = DialogNameEntryBinding.inflate(LayoutInflater.from(context));
        Dialog dialog = new Dialog(context);
        dialog.setContentView(binding.getRoot());

        // When the OK button is clicked, check if the name is valid.
        binding.btnOk.setOnClickListener(v -> {
            String name = binding.editTextPictureName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(context, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                return; // Do not dismiss the dialog if the name is empty.
            }
            // If a valid name is entered, call the callback.
            if (listener != null) {
                listener.onNameEntered(name);
            }
            // Dismiss the dialog.
            dialog.dismiss();
        });

        dialog.show();
    }








}
