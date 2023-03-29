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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentCameraBinding;
import de.uniaugsburg.app.ui.add.AddViewModel;
import de.uniaugsburg.app.ui.home.HomeFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CameraFragment extends Fragment implements View.OnClickListener {

    private FragmentCameraBinding binding;
    private View root;

    private CameraViewModel cameraViewModel;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cameraViewModel =
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
            Toast.makeText(getContext(), R.string.no_permission_text, Toast.LENGTH_SHORT).show();
        }

        binding.label.setText(R.string.pre_model_text);
        binding.searchButton.setText(getString(R.string.search));
        binding.weight.setText(getString(R.string.weight));

        binding.searchButton.setOnClickListener(this);

        binding.saveButton.setVisibility(View.GONE);
        binding.weight.setVisibility(View.GONE);

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // DATEI SPEICHERN
            }
        });


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
            sendImageRequest(imageBitmap);
        }
    }

    private boolean checkCameraPermission() {
        int permissionResult = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA);
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void sendImageRequest(Bitmap imageBitmap) {
        OkHttpClient client = new OkHttpClient();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg",
                        RequestBody.create(MediaType.parse("image/jpeg"), imageData))
                .build();

        Request request = new Request.Builder()
                .url("https://0943-137-250-27-5.eu.ngrok.io")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("E", "Error (sending)");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        Log.d("E", "Error (no body)");
                    } else {
                        String resultString = responseBody.string();
                        // handle the response
                        binding.inputField.setText(resultString);
                    }
                } else {
                    Log.d("E", "Error (unsuccessful) " + response.code());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        cameraViewModel.changeValue();
    }
}