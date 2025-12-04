package com.mobileapp.styler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.mobileapp.styler.databinding.FragmentPickShoeBinding;

public class PickShoeFragment extends Fragment {
    private FragmentPickShoeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPickShoeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.pickShoeGrid.setAdapter(new PlaceholderAdapter());

        binding.nextButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickShoeFragment.this)
                    .navigate(R.id.action_pickShoeFragment_to_visualizationFragment);
        });

        binding.backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickShoeFragment.this).popBackStack();
        });
    }
}
