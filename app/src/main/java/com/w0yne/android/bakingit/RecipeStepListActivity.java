package com.w0yne.android.bakingit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.w0yne.android.bakingit.data.Recipe;
import com.w0yne.android.bakingit.data.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class RecipeStepListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @VisibleForTesting
    Recipe mRecipe;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recipe_step_list)
    RecyclerView mRecipeStepRcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecipe = Realm.getDefaultInstance().where(Recipe.class)
                .equalTo("uid", getIntent().getIntExtra(Intents.EXTRA_RECIPE_ID, -1))
                .findFirst();
        assert mRecipe != null;

        setContentView(R.layout.activity_recipe_step_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setTitle(mRecipe.name);

        setupRecyclerView(mRecipeStepRcv);

        mTwoPane = Utility.isTablet(this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new RecipeStepListAdapter(mRecipe));
    }

    public class RecipeStepListAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Recipe mRecipe;

        public RecipeStepListAdapter(Recipe recipe) {
            mRecipe = recipe;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case R.id.view_type_ingredient:
                    View ingredientItemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recipe_ingredent_description, parent, false);
                    return new IngredientViewHolder(ingredientItemView);
                case R.id.view_type_step:
                    View stepItemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.recipe_step_description, parent, false);
                    return new StepViewHolder(stepItemView);
                default:
                    throw new IllegalArgumentException("Unknown viewType");
            }

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof StepViewHolder) {
                StepViewHolder stepViewHolder = (StepViewHolder) holder;
                stepViewHolder.mStep = mRecipe.stepList.get(position - 1);
                stepViewHolder.mDescriptionTxv.setText(stepViewHolder.mStep.shortDescription);
                stepViewHolder.mView.setOnClickListener(v -> {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(RecipeStepDetailFragment.ARG_STEP_ID, stepViewHolder.mStep.uid);
                        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                        fragment.setArguments(arguments);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.recipe_step_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipeStepDetailActivity.class);
                        intent.putExtra(Intents.EXTRA_RECIPE_ID, mRecipe.uid);
                        intent.putExtra(RecipeStepDetailFragment.ARG_STEP_ID, stepViewHolder.mStep.uid);

                        context.startActivity(intent);
                    }
                });
            } else if (holder instanceof IngredientViewHolder) {
                IngredientViewHolder ingredientViewHolder = (IngredientViewHolder) holder;
                ingredientViewHolder.mDescriptionTxv.setText(mRecipe.getDisplayIngredients());
            }
        }

        @Override
        public int getItemViewType(final int position) {
            if (position == 0) {
                return R.id.view_type_ingredient;
            } else {
                return R.id.view_type_step;
            }
        }

        @Override
        public int getItemCount() {
            return mRecipe.stepList.size() + 1;
        }

        public class IngredientViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            @BindView(R.id.description)
            public TextView mDescriptionTxv;

            public IngredientViewHolder(final View itemView) {
                super(itemView);
                mView = itemView;
                ButterKnife.bind(this, itemView);
            }
        }

        public class StepViewHolder extends RecyclerView.ViewHolder {
            public View mView;
            @BindView(R.id.description)
            public TextView mDescriptionTxv;
            public Step mStep;

            public StepViewHolder(View view) {
                super(view);
                mView = view;
                ButterKnife.bind(this, view);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mDescriptionTxv.getText() + "'";
            }
        }
    }
}
