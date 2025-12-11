package com.mobileapp.styler;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

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

    public interface OnOutfitClickListener {
        void onOutfitClick(Outfit outfit);
    }

    public void setOnOutfitClickListener(OnOutfitClickListener listener) {
        this.clickListener = listener;
    }

    public void setOutfits(List<Outfit> outfits) {
        this.outfits = outfits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OutfitViewHolder(
                (ViewGroup) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_outfit, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder holder, int position) {
        holder.bind(outfits.get(position), clickListener);
    }

    @Override
    public int getItemCount() {
        return outfits.size();
    }

    static class OutfitViewHolder extends RecyclerView.ViewHolder {

        private final ImageView topImage;
        private final ImageView bottomImage;
        private final ImageView shoeImage;

        public OutfitViewHolder(@NonNull ViewGroup itemView) {
            super(itemView);
            topImage = itemView.findViewById(R.id.outfit_top_image);
            bottomImage = itemView.findViewById(R.id.outfit_bottom_image);
            shoeImage = itemView.findViewById(R.id.outfit_shoe_image);
        }

        public void bind(Outfit outfit, OnOutfitClickListener listener) {
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

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOutfitClick(outfit);
                }
            });
        }
    }
}
