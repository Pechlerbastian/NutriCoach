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

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentCameraBinding;
import de.uniaugsburg.app.ui.home.HomeFragment;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    private View root;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CameraViewModel cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        // check for camera permission and request camera permission if no given
        if (!checkCameraPermission()) {
            requestCameraPermission();
        }
        // if permission was denied return to landing page
        if (!checkCameraPermission()) {
            // switch to landing page TODO
            // Get the FragmentManager
            FragmentManager fragmentManager = getParentFragmentManager();

            // Begin a FragmentTransaction
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Hide FragmentA and show FragmentB
            fragmentTransaction.replace(R.id.container, HomeFragment.class, null);

            // Add the transaction to the back stack
            fragmentTransaction.addToBackStack(null);

            // Commit the FragmentTransaction
            fragmentTransaction.commit();

            return root;
        }

        // Launch camera intent to take a photo
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
        Context context = getContext();
        if (context == null) {
            return false;
        }
        int permissionResult = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }
}