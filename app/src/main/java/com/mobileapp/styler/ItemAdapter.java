package com.mobileapp.styler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.mobileapp.styler.db.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> items = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = items.get(position);
        holder.bind(currentItem, clickListener, longClickListener, position, selectedPosition);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Item item);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView image;
        private final MaterialCardView cardView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            image = itemView.findViewById(R.id.item_image);
            cardView = (MaterialCardView) itemView;
        }

        public void bind(final Item item, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener, final int position, final int selectedPosition) {
            name.setText(item.name);
            if (item.imagePath != null && !item.imagePath.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(new File(item.imagePath))
                        .into(image);
            } else {
                image.setImageResource(R.mipmap.ic_launcher);
            }

            boolean isSelected = position == selectedPosition;
            cardView.setChecked(isSelected);

            if (isSelected) {
                cardView.setCardElevation(16f);
                cardView.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.yellowAccent));
                cardView.setStrokeWidth(4);
            } else {
                cardView.setCardElevation(0f);
                cardView.setStrokeColor(ContextCompat.getColor(itemView.getContext(), R.color.black));
                cardView.setStrokeWidth(2);
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(item);
                    int previousPosition = ItemAdapter.this.selectedPosition;
                    if (previousPosition == getAdapterPosition()) {
                        // Deselect the item
                        ItemAdapter.this.selectedPosition = RecyclerView.NO_POSITION;
                    } else {
                        ItemAdapter.this.selectedPosition = getAdapterPosition();
                    }
                    notifyItemChanged(previousPosition);
                    notifyItemChanged(ItemAdapter.this.selectedPosition);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(item);
                    return true; // Consume the long click
                }
                return false;
            });
        }
    }
}
