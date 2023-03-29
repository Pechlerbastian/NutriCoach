package de.uniaugsburg.app.ui.add;
import static java.lang.Math.round;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentAddBinding;
import de.uniaugsburg.app.util.JsonParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFragment extends Fragment implements View.OnClickListener {

    private FragmentAddBinding binding;
    private AddViewModel addViewModel;

    private Context context;

    private String[] saveVal;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        context = this.requireContext();

        addViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);


        binding = FragmentAddBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        addViewModel.getText().observe(getViewLifecycleOwner(), binding.previewField::setText);


        Objects.requireNonNull(binding.inputField.getEditText()).setText(getString(R.string.input));
        binding.searchButton.setText(getString(R.string.search));
        Objects.requireNonNull(binding.weight.getEditText()).setText(getString(R.string.weight));

        binding.searchButton.setOnClickListener(this);

        binding.saveButton.setVisibility(View.GONE);
        binding.weight.setVisibility(View.GONE);

        binding.saveButton.setOnClickListener(v -> {
            Map<String, List<Integer>>  itemKcalMap = JsonParser.parseJsonFromAsset(context);

            // TODO : foodName = saveVal[0] caloriesPer100 = saveVal[1]
            String foodName = "dummyFoodItems";
            String caloriesPer100 = "120";

            int calories = Integer.parseInt(caloriesPer100);

            String weight = binding.weight.getEditText().getText().toString();
            float amount = Integer.parseInt(weight);
            Integer totalCalories = round(amount / 100 * calories);
            List<Integer> list = Collections.singletonList(totalCalories);

            itemKcalMap.put(foodName, list);

            try {
                JsonParser.writeJson(itemKcalMap, context);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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
    }

    @Override
    public void onClick(View view) {
        binding.saveButton.setVisibility(View.VISIBLE);
        binding.weight.setVisibility(View.VISIBLE);
        RadioButton btn = root.findViewById(binding.radioGroup.getCheckedRadioButtonId());
        String type = btn.getText().toString();

        String name = Objects.requireNonNull(binding.inputField.getEditText()).getText().toString();

        saveVal = addViewModel.changeValue(type, name);
    }
}