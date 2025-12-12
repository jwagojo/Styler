package com.mobileapp.styler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import com.mobileapp.styler.databinding.FragmentPickTopBinding;
import com.mobileapp.styler.db.Item;

public class PickTopFragment extends Fragment {

    private FragmentPickTopBinding binding;
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
        binding = FragmentPickTopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();

        viewModel.tops.observe(getViewLifecycleOwner(), tops -> {
            adapter.setItems(tops);
        });

        binding.fabNext.setOnClickListener(v -> {
            if (viewModel.getSelectedTop().getValue() != null) {
                NavHostFragment.findNavController(PickTopFragment.this)
                        .navigate(R.id.action_pickTopFragment_to_pickBottomFragment);
            } else {
                Toast.makeText(getContext(), getString(R.string.select_item_toast), Toast.LENGTH_SHORT).show();
            }
        });

        binding.fabBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(PickTopFragment.this).popBackStack();
        });
    }

    private void setupRecyclerView() {
        binding.pickTopGrid.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ItemAdapter();
        adapter.setOnItemClickListener(item -> {
            viewModel.setSelectedTop(item);
        });
        binding.pickTopGrid.setAdapter(adapter);
    }
}
