package de.uniaugsburg.app.ui.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentAddBinding;

public class AddFragment extends Fragment implements View.OnClickListener {

    private FragmentAddBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddViewModel dashboardViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.inputField.setText(getString(R.string.input));
        binding.searchButton.setText(getString(R.string.search));
        binding.label.setText(getString(R.string.label));

        binding.searchButton.setOnClickListener(this);
        binding.button.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        String searchText =  binding.inputField.getText().toString();

        if(binding.radioGroup.getCheckedRadioButtonId() == R.id.radio_recipe) {
            binding.previewField.setText("You selected a recipe" + searchText);
        } else {
            binding.previewField.setText("You selected a ingredient" + searchText);
        }
    }
}