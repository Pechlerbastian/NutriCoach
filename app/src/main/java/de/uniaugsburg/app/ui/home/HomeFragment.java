package de.uniaugsburg.app.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentHomeBinding;
import de.uniaugsburg.app.ui.settings.SettingsFragment;
import de.uniaugsburg.app.util.JsonParser;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ViewGroup ViewContainer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        ViewContainer = container;

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
        Map<String, String> userData = JsonParser.parseUserDataJson(getContext());
        String maxCalories;

        if (userData == null || userData.isEmpty()) {
            Toast.makeText(getContext(), "Please make a user profile", Toast.LENGTH_SHORT).show();
            maxCalories = "/2500";
            /*FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            List<Fragment> fragments = fragmentManager.getFragments();

            Fragment settingsFragment = null;
            for (Fragment fragment:fragments) {
                if (fragment instanceof SettingsFragment) {
                    settingsFragment = fragment;
                    break;
                }
            }
            if (settingsFragment == null) {
                settingsFragment = new SettingsFragment();
            }
            fragmentTransaction.replace(ViewContainer.getId(), settingsFragment);
            fragmentTransaction.commit();
            return;*/
        } else {
            maxCalories = '/' + userData.get("Caloric Demand");
        }

        Map<String, List<Integer>> itemKcalMap = JsonParser.parseJson(this.requireContext());
        Integer calories = 0;
        for (String key : itemKcalMap.keySet()) {
            calories += Objects.requireNonNull(itemKcalMap.get(key)).get(0);
        }
        String consumedCalories = String.format(getString(R.string.calories), calories);
        binding.caloriesText.setText(consumedCalories);

        binding.maxCaloriesText.setText(maxCalories);
    }
}