package de.uniaugsburg.app.ui.add;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import java.io.IOException;

import de.uniaugsburg.app.R;
import de.uniaugsburg.app.databinding.FragmentAddBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFragment extends Fragment implements View.OnClickListener {

    private FragmentAddBinding binding;
    private AddViewModel addViewModel;

    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        addViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);


        binding = FragmentAddBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        addViewModel.getText().observe(getViewLifecycleOwner(), binding.previewField::setText);


        binding.inputField.setText(getString(R.string.input));
        binding.searchButton.setText(getString(R.string.search));
        binding.label.setText(getString(R.string.label));
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        RadioButton btn = root.findViewById(binding.radioGroup.getCheckedRadioButtonId());
        String type = btn.getText().toString();

        String name = binding.inputField.getText().toString();

        addViewModel.changeValue(type, name);
    }
}