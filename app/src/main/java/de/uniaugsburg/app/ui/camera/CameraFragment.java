package de.uniaugsburg.app.ui.camera;

import static android.app.Activity.RESULT_OK;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentCameraBinding;
import de.uniaugsburg.app.ui.home.HomeFragment;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private View root;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CameraViewModel cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        if (checkCameraPermission()) {
            // Launch camera intent to take a photo
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            final TextView textView = binding.textView;
            textView.setText(R.string.no_permission_text);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // receive photo taken and give ml model for prediction TODO
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Handle photo data
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // rework from here
            ImageView imageView = root.findViewById(R.id.image_view);
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private boolean checkCameraPermission() {
        int permissionResult = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }
}