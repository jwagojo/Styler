package com.mobileapp.styler;

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
import com.mobileapp.styler.databinding.FragmentPickShoeBinding;
import com.mobileapp.styler.db.Item;

public class PickShoeFragment extends Fragment {

    private FragmentPickShoeBinding binding;
    private StylerViewModel viewModel;
    private ItemAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(StylerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPickShoeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        viewModel.shoes.observe(getViewLifecycleOwner(), shoes -> {
            adapter.setItems(shoes);
        });

        binding.nextButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickShoeFragment.this)
                    .navigate(R.id.action_pickShoeFragment_to_visualizationFragment);
        });

        binding.fabBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickShoeFragment.this).popBackStack();
        });
    }

    private void setupRecyclerView() {
        binding.pickShoeGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ItemAdapter();
        adapter.setOnItemClickListener(item -> {
            viewModel.setSelectedShoe(item);
        });
        binding.pickShoeGrid.setAdapter(adapter);
    }
}
