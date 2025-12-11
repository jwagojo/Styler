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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mobileapp.styler.databinding.FragmentAllOutfitsBinding;
import com.mobileapp.styler.db.AppDatabase;
import com.mobileapp.styler.db.Outfit;

public class AllOutfitsFragment extends Fragment {

    private FragmentAllOutfitsBinding binding;
    private OutfitAdapter outfitAdapter;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAllOutfitsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getDatabase(requireContext());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        binding.allOutfitsRecycler.setLayoutManager(gridLayoutManager);
        outfitAdapter = new OutfitAdapter();
        outfitAdapter.setAddCard(false);
        binding.allOutfitsRecycler.setAdapter(outfitAdapter);

        db.outfitDao().getAllOutfits().observe(
                getViewLifecycleOwner(),
                outfits -> outfitAdapter.setOutfits(outfits)
        );
        binding.backButton.setOnClickListener(v ->
                NavHostFragment.findNavController(AllOutfitsFragment.this).navigateUp()
        );

        outfitAdapter.setOnOutfitClickListener(new OutfitAdapter.OnOutfitClickListener() {
            @Override
            public void onOutfitClick(Outfit outfit) {

            }

            @Override
            public void onAddNewOutfit() {

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
