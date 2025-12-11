package com.mobileapp.styler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobileapp.styler.db.Outfit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder> {

    private List<Outfit> outfits = new ArrayList<>();
    private OnOutfitClickListener clickListener;

    private static final int VIEW_TYPE_ADD = 0;
    private static final int VIEW_TYPE_OUTFIT = 1;
    private boolean showAddCard;

    public interface OnOutfitClickListener {
        void onOutfitClick(Outfit outfit);
        void onAddNewOutfit();
    }

    public void setOnOutfitClickListener(OnOutfitClickListener listener) {
        this.clickListener = listener;
    }

    public void setAddCard(boolean showAddCard) {
        this.showAddCard = showAddCard;
    }
    public void setOutfits(List<Outfit> outfits) {
        this.outfits = outfits != null ? outfits : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        // position 0 is the "+" card
        if (!showAddCard) {
            return VIEW_TYPE_OUTFIT;
        }
        return (position == 0) ? VIEW_TYPE_ADD : VIEW_TYPE_OUTFIT;
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater inflater = LayoutInflater.from(parent.getContext());
       View view;
       if (viewType == VIEW_TYPE_ADD) {
           view = inflater.inflate(R.layout.item_outfit_add, parent, false);
       } else {
           view = inflater.inflate(R.layout.item_outfit, parent, false);
       }

        return new OutfitViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_ADD) {
            holder.bindAddCard(clickListener);
        } else {
            Outfit outfit;
            if (showAddCard) {
                outfit = outfits.get(position - 1);
            } else {
                outfit = outfits.get(position);
            }
            holder.bind(outfit, clickListener);
        }
    }

    @Override
    public int getItemCount() {
        if (showAddCard) {
            return outfits.size() + 1;  // +1 for "+"
        } else {
            return outfits.size();
        }
    }

    static class OutfitViewHolder extends RecyclerView.ViewHolder {

        private final ImageView topImage;
        private final ImageView bottomImage;
        private final ImageView shoeImage;
        private final ImageView addIcon;
        private final TextView outfitName;
        private final int viewType;

        public OutfitViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == VIEW_TYPE_ADD) {
                addIcon = itemView.findViewById(R.id.add_outfit_icon);
                topImage = null;
                bottomImage = null;
                shoeImage = null;
                outfitName = null;
            } else {
                addIcon = null;
                topImage = itemView.findViewById(R.id.outfit_top_image);
                bottomImage = itemView.findViewById(R.id.outfit_bottom_image);
                shoeImage = itemView.findViewById(R.id.outfit_shoe_image);
                outfitName = itemView.findViewById(R.id.outfit_name);
            }
        }

        public void bindAddCard(OnOutfitClickListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddNewOutfit();
                }
            });
        }

        public void bind(Outfit outfit, OnOutfitClickListener listener) {
            if (viewType == VIEW_TYPE_ADD) return;
            if (outfit.topImageUri != null) {
                Glide.with(itemView.getContext())
                        .load(new File(outfit.topImageUri))
                        .into(topImage);
            }
            if (outfit.bottomImageUri != null) {
                Glide.with(itemView.getContext())
                        .load(new File(outfit.bottomImageUri))
                        .into(bottomImage);
            }
            if (outfit.shoeImageUri != null) {
                Glide.with(itemView.getContext())
                        .load(new File(outfit.shoeImageUri))
                        .into(shoeImage);
            }
            if (outfitName != null) {
                String label = (outfit.name != null && !outfit.name.isEmpty())
                        ? outfit.name
                        : "Outfit " + outfit.id;
                outfitName.setText(label);
            }


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOutfitClick(outfit);
                }
            });
        }
    }
}
