package com.mobileapp.styler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.bumptech.glide.Glide;
import com.mobileapp.styler.databinding.FragmentVisualizationBinding;
import com.mobileapp.styler.db.AppDatabase;
import com.mobileapp.styler.db.Outfit;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        binding.homeButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(VisualizationFragment.this)
                    .navigate(R.id.action_visualizationFragment_to_catalogFragment);
        });

        binding.storeButton.setOnClickListener(v -> {
            storeOutfit();
        });
    }

    private void storeOutfit() {
        String topImagePath = viewModel.getSelectedTop().getValue() != null ? viewModel.getSelectedTop().getValue().imagePath : null;
        String bottomImagePath = viewModel.getSelectedBottom().getValue() != null ? viewModel.getSelectedBottom().getValue().imagePath : null;
        String shoeImagePath = viewModel.getSelectedShoe().getValue() != null ? viewModel.getSelectedShoe().getValue().imagePath : null;

        if (topImagePath != null && bottomImagePath != null && shoeImagePath != null) {
            Outfit outfit = new Outfit();
            outfit.topImageUri = topImagePath;
            outfit.bottomImageUri = bottomImagePath;
            outfit.shoeImageUri = shoeImagePath;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                AppDatabase.getDatabase(requireContext()).outfitDao().insert(outfit);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Outfit saved!", Toast.LENGTH_SHORT).show();
                });
            });
        } else {
            Toast.makeText(requireContext(), "Cannot save incomplete outfit.", Toast.LENGTH_SHORT).show();
        }
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
