package com.mobileapp.styler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.mobileapp.styler.databinding.FragmentVisualizationBinding;
import com.mobileapp.styler.db.Item;

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

        viewModel.getSelectedTop().observe(getViewLifecycleOwner(), top -> {
            if (top != null) {
                loadImage(top.imagePath, binding.topImage);
            }
        });

        viewModel.getSelectedBottom().observe(getViewLifecycleOwner(), bottom -> {
            if (bottom != null) {
                loadImage(bottom.imagePath, binding.bottomImage);
            }
        });

        viewModel.getSelectedShoe().observe(getViewLifecycleOwner(), shoe -> {
            if (shoe != null) {
                loadImage(shoe.imagePath, binding.shoeImage);
            }
        });

        binding.homeButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(VisualizationFragment.this)
                    .navigate(R.id.action_visualizationFragment_to_catalogFragment);
        });

        binding.storeButton.setOnClickListener(v -> {
            // TODO: Implement navigation to the store when it is created
        });
    }

    private void loadImage(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(requireContext())
                    .load(new File(imagePath))
                    .into(imageView);
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher); // Fallback image
        }
    }
}
