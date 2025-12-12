package com.mobileapp.styler;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mobileapp.styler.databinding.FragmentCatalogBinding;
import com.mobileapp.styler.db.AppDatabase;
import com.mobileapp.styler.db.Item;

public class CatalogFragment extends Fragment {

    private FragmentCatalogBinding binding;
    private ItemAdapter adapter;
    private StylerViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(StylerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        AppDatabase db = AppDatabase.getDatabase(requireContext());
        db.itemDao().getAllItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
        });

        binding.fabAddItem.setOnClickListener(v -> {
            NavHostFragment.findNavController(CatalogFragment.this)
                    .navigate(R.id.action_catalogFragment_to_addItemFragment);
        });

        binding.fabNext.setOnClickListener(v -> {
            NavHostFragment.findNavController(CatalogFragment.this)
                    .navigate(R.id.action_catalogFragment_to_pickTopFragment);
        });
    }

    private void setupRecyclerView() {
        /*binding.catalogGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ItemAdapter();

        adapter.setOnItemLongClickListener(item -> {
            showDeleteConfirmationDialog(item);
        }); */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        binding.catalogGrid.setLayoutManager(gridLayoutManager);
        adapter = new ItemAdapter();
        adapter.setOnItemLongClickListener(item -> {
            showDeleteConfirmationDialog(item);
        });

        binding.catalogGrid.setAdapter(adapter);
    }

    private void showDeleteConfirmationDialog(Item item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete '" + item.name + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteItem(item);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
