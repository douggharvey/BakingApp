package com.douglasharvey.bakingapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.douglasharvey.bakingapp.R;
import com.douglasharvey.bakingapp.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.ViewHolder> {

    private ArrayList<Recipe> recipeList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        Recipe recipe;

        @BindView(R.id.iv_master_recipe_image)
        ImageView ivMasterRecipeImage;
        @BindView(R.id.tv_master_recipe_name)
        TextView tvMasterRecipeName;
        @BindView(R.id.tv_recipe_number_servings)
        TextView tvRecipeNumberServings;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setRecipesData(ArrayList<Recipe> recipeList) {
        if (recipeList != null) {
            this.recipeList = recipeList;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View inflatedView = inflater.inflate(R.layout.master_list_item, parent, false);

        return new ViewHolder(inflatedView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.recipe = recipeList.get(position);

        holder.tvMasterRecipeName.setText(holder.recipe.getName());
        holder.tvRecipeNumberServings.setText(Integer.toString(holder.recipe.getServings()));

        // no image data on json sample but in any case the code below has been tested by using a hard-coded image
        // ivMasterRecipeImage is displayed with a default image of a cupcake.
        if (holder.recipe.getImage() != null && !holder.recipe.getImage().trim().isEmpty()) {
            Picasso.with(holder.ivMasterRecipeImage.getContext())
                    .load(holder.recipe.getImage())
                    .placeholder(R.drawable.ic_cupcake)
                    .error(R.drawable.ic_cupcake)
                    .into(holder.ivMasterRecipeImage);
        }
    }

    @Override
    public int getItemCount() {
        if (recipeList == null) return 0;
        else return recipeList.size();
    }

}
