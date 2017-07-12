package com.w0yne.android.bakingit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.w0yne.android.bakingit.data.Recipe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private Context mContext;
    private List<Recipe> mRecipes;
    private OnItemClickListener mOnItemClickListener;

    public RecipeListAdapter(Context context) {
        mContext = context;
        mRecipes = new ArrayList<>();
    }

    public void setData(List<Recipe> recipes) {
        mRecipes.clear();
        mRecipes.addAll(recipes);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ViewHolder viewHolder, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.recipe_card_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Recipe recipe = mRecipes.get(position);
        holder.data = recipe;
        holder.recipeNameTxv.setText(recipe.name);
        holder.recipeNameTxv.setOnClickListener(v -> mOnItemClickListener.onItemClick(holder, position));
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_name_txv)
        TextView recipeNameTxv;
        public Recipe data;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
