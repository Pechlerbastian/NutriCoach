package de.uniaugsburg.app.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentHomeBinding;
import de.uniaugsburg.app.util.JsonParser;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy\nhh:mm", Locale.US);
        binding.dateText.setText(dt.format(currentTime));

        Map<String, List<Integer>> itemKcalMap;
        itemKcalMap = JsonParser.parseJsonFromAsset(this.requireContext());
        Integer calories = 0;
        for (String key : itemKcalMap.keySet()) {
            calories += itemKcalMap.get(key).get(0);
        }
        String consumedCalories = String.format(getString(R.string.calories), calories);
        binding.caloriesText.setText(consumedCalories);

        String maxCalories = '/' + String.valueOf(4000);
        binding.maxCaloriesText.setText(maxCalories);
    }
}