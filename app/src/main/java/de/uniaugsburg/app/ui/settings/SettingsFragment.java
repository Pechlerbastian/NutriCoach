package de.uniaugsburg.app.ui.settings;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentSettingsBinding;
import de.uniaugsburg.app.util.JsonParser;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;

    private FragmentSettingsBinding binding;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        Spinner spinner = binding.activityLevel;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.activity_levels, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        binding.saveButton.setOnClickListener(v -> {
            String AgeStr = Objects.requireNonNull(binding.userAge.getEditText()).getText().toString();
            if (AgeStr.equals("")) {
                Toast.makeText(getContext(), "Please type in your age", Toast.LENGTH_SHORT).show();
                return;
            }
            int age = Integer.parseInt(AgeStr);

            String WeightStr = Objects.requireNonNull(binding.userWeight.getEditText()).getText().toString();
            if (WeightStr.equals("")) {
                Toast.makeText(getContext(), "Please type in your weight", Toast.LENGTH_SHORT).show();
                return;
            }
            int weight = Integer.parseInt(WeightStr);

            String HeightStr = Objects.requireNonNull(binding.userHeight.getEditText()).getText().toString();
            if (HeightStr.equals("")) {
                Toast.makeText(getContext(), "Please type in your height", Toast.LENGTH_SHORT).show();
                return;
            }
            int height = Integer.parseInt(HeightStr);

            RadioButton btn = root.findViewById(binding.radioGroup.getCheckedRadioButtonId());
            String gender = btn.getText().toString();

            String act_level = spinner.getSelectedItem().toString();

            int daily_allowance = calculateCaloricDemand(age, weight, height, gender, act_level);

            String consumedCalories = String.format(getString(R.string.caloric_demand), daily_allowance);
            binding.result.setText(consumedCalories);

            Map<String, String> userData = new HashMap<>();
            userData.put("Age", AgeStr);
            userData.put("Weight", WeightStr);
            userData.put("Height", HeightStr);
            userData.put("Gender", gender);
            userData.put("Activity Level", act_level);
            userData.put("Caloric Demand", Integer.toString(daily_allowance));

            try {
                JsonParser.writeJsonUserData(userData, getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();
        Map<String, String> userData = JsonParser.parseUserDataJson(getContext());
        String gender, act_level;
        if (userData != null && !userData.isEmpty()) {
            Objects.requireNonNull(binding.userAge.getEditText()).setText(userData.get("Age"));
            Objects.requireNonNull(binding.userWeight.getEditText()).setText(userData.get("Weight"));
            Objects.requireNonNull(binding.userHeight.getEditText()).setText(userData.get("Height"));

            gender = userData.get("Gender");
            switch (Objects.requireNonNull(gender)) {
                case "Male":
                    binding.radioMale.setChecked(true);
                    break;
                case "Female":
                    binding.radioFemale.setChecked(true);
                    break;
                case "Other":
                    binding.radioOther.setChecked(true);
                    break;
            }

            act_level = userData.get("Activity Level");
            switch (Objects.requireNonNull(act_level)) {
                case "Sedentary":
                    binding.activityLevel.setSelection(0);
                    break;
                case "Lightly Active":
                    binding.activityLevel.setSelection(1);
                    break;
                case "Moderately Active":
                    binding.activityLevel.setSelection(2);
                    break;
                case "Very Active":
                    binding.activityLevel.setSelection(3);
                    break;
                case "Extra Active":
                    binding.activityLevel.setSelection(4);
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int calculateCaloricDemand(int age, int weight, int height, String gender, String act_level) {
        int gender_offset;
        double activity_factor, bmr, caloric_demand;
        switch (gender) {
            case "Male":
                gender_offset = 5;
                break;
            case "Female":
                gender_offset = -161;
                break;
            case "Other":
                gender_offset = -78;
                break;
            default:
                gender_offset = 100000;
        }

        switch (act_level) {
            case "Sedentary":
                activity_factor = 1.2;
                break;
            case "Lightly Active":
                activity_factor = 1.375;
                break;
            case "Moderately Active":
                activity_factor = 1.55;
                break;
            case "Very Active":
                activity_factor = 1.725;
                break;
            case "Extra Active":
                activity_factor = 1.9;
                break;
            default:
                activity_factor = 30;
        }

        bmr = 10 * weight + 6.25 * height - 5 * age + gender_offset;

        caloric_demand = bmr * activity_factor;

        return (int) Math.round(caloric_demand);
    }

}