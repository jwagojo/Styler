package com.mobileapp.styler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mobileapp.styler.databinding.FragmentCatalogBinding;
import com.mobileapp.styler.db.AppDatabase;

public class CatalogFragment extends Fragment {

    private FragmentCatalogBinding binding;
    private ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.catalogGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ItemAdapter();
        binding.catalogGrid.setAdapter(adapter);

        AppDatabase db = AppDatabase.getDatabase(requireContext());
        db.itemDao().getAllItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
        });

        binding.nextButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(CatalogFragment.this)
                    .navigate(R.id.action_catalogFragment_to_pickTopFragment);
        });

        binding.fabAddItem.setOnClickListener(v -> {
            NavHostFragment.findNavController(CatalogFragment.this)
                    .navigate(R.id.action_catalogFragment_to_addItemFragment);
        });
    }
}
