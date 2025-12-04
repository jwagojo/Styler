package com.mobileapp.styler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.mobileapp.styler.databinding.FragmentPickBottomBinding;

public class PickBottomFragment extends Fragment {

    private FragmentPickBottomBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPickBottomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.pickBottomGrid.setAdapter(new PlaceholderAdapter());

        binding.nextButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickBottomFragment.this)
                    .navigate(R.id.action_pickBottomFragment_to_pickShoeFragment);
        });

        binding.backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickBottomFragment.this).popBackStack();
        });
    }
}
