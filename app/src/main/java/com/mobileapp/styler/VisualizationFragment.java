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
        int outfitId = -1;
        Bundle args = getArguments();
        if (args != null) {
            outfitId = args.getInt("outfitId", -1);
        }
        if (outfitId != -1) {
            // Behavior A: viewing a saved outfit
            loadSavedOutfit(outfitId);
        } else {
            // Existing behavior: new outfit flow using ViewModel
            observeViewModel();
        }
        observeViewModel();

        binding.homeButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(VisualizationFragment.this)
                    .navigate(R.id.action_visualizationFragment_to_homeFragment);
        });

        binding.storeButton.setOnClickListener(v -> {
            //storeOutfit();
            askOutfitName();
        });
    }

    private void loadSavedOutfit(int id) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Outfit outfit = AppDatabase.getDatabase(requireContext())
                    .outfitDao()
                    .getOutfitById(id);

            if (outfit != null) {
                requireActivity().runOnUiThread(() -> {
                    Glide.with(this).load(new File(outfit.topImageUri)).into(binding.topImage);
                    Glide.with(this).load(new File(outfit.bottomImageUri)).into(binding.bottomImage);
                    Glide.with(this).load(new File(outfit.shoeImageUri)).into(binding.shoeImage);
                });
            } //test
        });
    }

    private void storeOutfit(String outfitName) {
        String topImagePath = viewModel.getSelectedTop().getValue() != null ? viewModel.getSelectedTop().getValue().imagePath : null;
        String bottomImagePath = viewModel.getSelectedBottom().getValue() != null ? viewModel.getSelectedBottom().getValue().imagePath : null;
        String shoeImagePath = viewModel.getSelectedShoe().getValue() != null ? viewModel.getSelectedShoe().getValue().imagePath : null;

        if (topImagePath != null && bottomImagePath != null && shoeImagePath != null) {
            Outfit outfit = new Outfit();
            outfit.topImageUri = topImagePath;
            outfit.bottomImageUri = bottomImagePath;
            outfit.shoeImageUri = shoeImagePath;
            outfit.name = outfitName;

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

    private void askOutfitName() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Name Your Outfit");

        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setHint("e.g., Summer Fit, Black & White");
        input.setPadding(40, 30, 40, 30);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = input.getText().toString().trim();
            storeOutfit(name.isEmpty() ? "Untitled Outfit" : name);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
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
