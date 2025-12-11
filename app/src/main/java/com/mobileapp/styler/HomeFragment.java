package com.mobileapp.styler;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mobileapp.styler.databinding.FragmentHomeBinding;
import com.mobileapp.styler.db.AppDatabase;
import com.mobileapp.styler.db.Item;
import com.mobileapp.styler.db.ItemDao;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ItemAdapter itemAdapter;
    private List<Item> allItems = new ArrayList<>();
    private OutfitAdapter outfitAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        binding.clothingGallery.setLayoutManager(gridLayoutManager);
        binding.clothingGallery.setHasFixedSize(true);

        itemAdapter = new ItemAdapter();
        binding.clothingGallery.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(item -> {
            // e.g., HomeFragmentDirections.actionHomeFragmentToItemDetailFragment(item.id)
        });

        itemAdapter.setOnItemLongClickListener(item -> {
        });

        outfitAdapter = new OutfitAdapter();
        binding.outfitsRecycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                requireContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
                );
        binding.outfitsRecycler.setAdapter(outfitAdapter);
        outfitAdapter.setOnOutfitClickListener(outfit -> {
            // later
        });

        binding.edittextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterItems(s.toString());
            }
        });

        displayAllItems();
        return binding.getRoot();
    }

    private void displayAllItems() {
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        ItemDao itemDao = db.itemDao();

        itemDao.getAllItems().observe(getViewLifecycleOwner(), items -> {
            allItems.clear();
            if (items != null) {
                allItems.addAll(items);
            }
            String query = "";
            if (binding.edittextSearch.getText() != null) {
                query = binding.edittextSearch.getText().toString();
            }
            filterItems(query);
        });
    }

    private void filterItems(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();

        if (q.isEmpty()) {
            itemAdapter.setItems(allItems);
            return;
        }

        List<Item> filtered = new ArrayList<>();
        for (Item item : allItems) {
            String name = item.name != null ? item.name.toLowerCase() : "";
            String type = item.type != null ? item.type.toLowerCase() : "";

            if (name.contains(q) || type.contains(q)) {
                filtered.add(item);
            }
        }

        itemAdapter.setItems(filtered);
    }

    private void observeAllOutfits() {
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        db.outfitDao().getAllOutfits().observe(getViewLifecycleOwner(), outfits -> {
            outfitAdapter.setOutfits(outfits);
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
