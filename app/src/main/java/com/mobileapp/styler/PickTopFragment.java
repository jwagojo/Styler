package com.mobileapp.styler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.mobileapp.styler.databinding.FragmentPickTopBinding;

public class PickTopFragment extends Fragment {

    private FragmentPickTopBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPickTopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.pickTopGrid.setAdapter(new PlaceholderAdapter());

        binding.nextButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickTopFragment.this)
                    .navigate(R.id.action_pickTopFragment_to_pickBottomFragment);
        });

        binding.backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickTopFragment.this).popBackStack();
        });
    }
}
