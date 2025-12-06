package com.mobileapp.styler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.mobileapp.styler.databinding.FragmentAddItemBinding;
import com.mobileapp.styler.db.AppDatabase;
import com.mobileapp.styler.db.Item;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddItemFragment extends Fragment {

    private FragmentAddItemBinding binding;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddItemBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSave.setOnClickListener(v -> {
            saveItem();
        });
    }

    private void saveItem() {
        String itemName = binding.editTextItemName.getText().toString().trim();
        String itemType = binding.editTextItemType.getText().toString().trim();

        if (itemName.isEmpty() || itemType.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Item item = new Item();
        item.name = itemName;
        item.type = itemType;
        // We will set a placeholder for the image path for now
        item.imagePath = "";

        executorService.execute(() -> {
            AppDatabase.getDatabase(requireContext()).itemDao().insert(item);
            // After saving, navigate back to the catalog on the main thread
            requireActivity().runOnUiThread(() -> {
                NavHostFragment.findNavController(AddItemFragment.this).popBackStack();
            });
        });
    }
}
