package de.uniaugsburg.app.ui.camera;

import static android.app.Activity.RESULT_OK;

import static java.lang.Math.round;

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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentAddBinding;
import de.uniaugsburg.app.databinding.FragmentCameraBinding;
import de.uniaugsburg.app.ui.add.AddViewModel;
import de.uniaugsburg.app.ui.home.HomeFragment;
import de.uniaugsburg.app.util.JsonParser;
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

    private Context context;
    private CameraViewModel cameraViewModel;

    private String[] saveVal;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = this.requireContext();

        cameraViewModel =
                new ViewModelProvider(this).get(CameraViewModel.class);

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        if (checkCameraPermission()) {
            // Launch camera intent to take a photo
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            }
        } else {
            Toast.makeText(getContext(), R.string.no_permission_text, Toast.LENGTH_SHORT).show();
        }

        cameraViewModel.getTextCamera().observe(getViewLifecycleOwner(), binding.editText::setText);
        cameraViewModel.getText().observe(getViewLifecycleOwner(), binding.previewField::setText);

        binding.searchButton.setText(getString(R.string.search));
        Objects.requireNonNull(binding.weight.getEditText()).setText(getString(R.string.weight));

        binding.searchButton.setOnClickListener(this);

        binding.saveButton.setVisibility(View.GONE);
        binding.weight.setVisibility(View.GONE);

        binding.saveButton.setOnClickListener(v -> {
            Map<String, List<Integer>> itemKcalMap = JsonParser.parseJsonFromAsset(context);

            String response = binding.previewField.getText().toString();
            if(response.equals("No matching items found")) {
                return;
            }
            String[] split_results = response.split("\n");
            String foodName = split_results[0];
            String caloriesPer100 = split_results[1].split(" ")[0];
            int totalCalories;

            int calories = Math.round(Float.parseFloat(caloriesPer100));

            RadioButton btn = root.findViewById(binding.radioGroup.getCheckedRadioButtonId());
            String type = btn.getText().toString();

            if (type.equals("Ingredient")) {
                String weight = binding.weight.getEditText().getText().toString();
                float amount = Integer.parseInt(weight);
                totalCalories = round(amount / 100 * calories);
            } else {
                totalCalories = calories;
            }
            List<Integer> list = Collections.singletonList(totalCalories);

            itemKcalMap.put(foodName, list);

            try {
                JsonParser.writeJsonCalories(itemKcalMap, context);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            Toast.makeText(getContext(), "Item added successfully!", Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.saveButton.setVisibility(View.GONE);
        binding.weight.setVisibility(View.GONE);
        binding.previewField.setText("");
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
                .url("https://2da4-137-250-27-6.eu.ngrok.io")
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
                        cameraViewModel.getMTextCamera().postValue(resultString);
                    }
                } else {
                    Log.d("E", "Error (unsuccessful) " + response.code());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        binding.saveButton.setVisibility(View.VISIBLE);
        RadioButton btn = root.findViewById(binding.radioGroup.getCheckedRadioButtonId());
        String type = btn.getText().toString();

        if (type.equals("Ingredient")) {
            binding.weight.setVisibility(View.VISIBLE);
        } else {
            binding.weight.setVisibility(View.GONE);
        }

        String name = Objects.requireNonNull(binding.inputField.getEditText()).getText().toString();

        saveVal = cameraViewModel.changeValue(type, name);
    }
}