package de.uniaugsburg.app.ui.add;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentDashboardBinding;

public class AddFragment extends Fragment implements View.OnClickListener {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel dashboardViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.searchButton.setOnClickListener(this);
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
        binding.inputField.setText(getString(R.string.input));
        binding.searchButton.setText(getString(R.string.search));
        binding.label.setText(getString(R.string.label));
    }

    @Override
    public void onClick(View view) {

        EditText searchText = (EditText) binding.inputField.getText();

        if(binding.radioGroup.getCheckedRadioButtonId() == R.id.radio_recipe) {
            binding.previewField.setText("You selected a recipe" + searchText);
        } else {
            binding.previewField.setText("You selected a ingredient" + searchText);

        }
    }
}