package com.mobileapp.styler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.bumptech.glide.Glide;
import com.mobileapp.styler.databinding.FragmentVisualizationBinding;
import java.io.File;

public class VisualizationFragment extends Fragment {

    private FragmentVisualizationBinding binding;
    private StylerViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(StylerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVisualizationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        observeViewModel();

        binding.backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(VisualizationFragment.this)
                    .navigate(R.id.action_visualizationFragment_to_catalogFragment);
        });
    }

    private void observeViewModel() {
        viewModel.getSelectedTop().observe(getViewLifecycleOwner(), top -> {
            if (top != null) {
                Glide.with(this).load(new File(top.imagePath)).into(binding.topImage);
            }
        });

        viewModel.getSelectedBottom().observe(getViewLifecycleOwner(), bottom -> {
            if (bottom != null) {
                Glide.with(this).load(new File(bottom.imagePath)).into(binding.bottomImage);
            }
        });

        viewModel.getSelectedShoe().observe(getViewLifecycleOwner(), shoe -> {
            if (shoe != null) {
                Glide.with(this).load(new File(shoe.imagePath)).into(binding.shoeImage);
            }
        });
    }
}
