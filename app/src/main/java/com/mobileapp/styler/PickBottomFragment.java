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
import com.mobileapp.styler.databinding.FragmentPickBottomBinding;
import com.mobileapp.styler.db.Item;

public class PickBottomFragment extends Fragment {

    private FragmentPickBottomBinding binding;
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
        binding = FragmentPickBottomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        viewModel.bottoms.observe(getViewLifecycleOwner(), bottoms -> {
            adapter.setItems(bottoms);
        });

        binding.nextButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickBottomFragment.this)
                    .navigate(R.id.action_pickBottomFragment_to_pickShoeFragment);
        });

        binding.backButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickBottomFragment.this).popBackStack();
        });
    }

    private void setupRecyclerView() {
        binding.pickBottomGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ItemAdapter();
        adapter.setOnItemClickListener(item -> {
            viewModel.setSelectedBottom(item);
        });
        binding.pickBottomGrid.setAdapter(adapter);
    }
}
