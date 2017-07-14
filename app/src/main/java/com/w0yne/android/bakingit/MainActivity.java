package com.w0yne.android.bakingit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.w0yne.android.bakingit.data.Recipe;
import com.w0yne.android.bakingit.net.ApiService;
import com.w0yne.android.bakingit.net.NetworkServiceGenerator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.recipe_cards_rcv)
    RecyclerView mRecipeCardsRcv;

    private RecipeListAdapter mRecipeListAdapter;
    private ApiService mApiService;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        GridLayoutManager layoutManager = new GridLayoutManager(this,
                Utility.isTablet(this) ? 3 : 1);
        mRecipeCardsRcv.setLayoutManager(layoutManager);
        mRecipeListAdapter = new RecipeListAdapter(this);
        mRecipeListAdapter.setOnItemClickListener((viewHolder, position) ->
                startActivity(new Intent(MainActivity.this, RecipeStepListActivity.class)
                        .putExtra(Intents.EXTRA_RECIPE_ID, viewHolder.data.uid)));
        mRecipeCardsRcv.setAdapter(mRecipeListAdapter);

        mApiService = NetworkServiceGenerator.createService(ApiService.class, NetworkServiceGenerator.getBaseUrl());

        mRefreshLayout.setOnRefreshListener(this::fetchData);

        if (Utility.isNetworkConnected(this)) {
            mRefreshLayout.setRefreshing(true);
            fetchData();
        }
    }

    private void fetchData() {
        mApiService.getRecipes()
                .enqueue(new Callback<List<Recipe>>() {
                    @Override
                    public void onResponse(final Call<List<Recipe>> call, final Response<List<Recipe>> response) {
                        mRefreshLayout.setRefreshing(false);
                        if (response.isSuccessful()) {
                            Log.i("Response", response.toString());
                            final Realm r = Realm.getDefaultInstance();
                            r.executeTransactionAsync(
                                    realm -> {
                                        List<Recipe> recipes = response.body();
                                        assert recipes != null;
                                        for (Recipe recipe : recipes) {
                                            recipe.normalizedData();
                                        }
                                        realm.insertOrUpdate(recipes);
                                    },
                                    () -> mRecipeListAdapter.setData(r.where(Recipe.class).findAll()),
                                    error -> onFailure(call, error));
                        }
                    }

                    @Override
                    public void onFailure(final Call<List<Recipe>> call, final Throwable t) {
                        Log.i("Error", t.getLocalizedMessage());
                        Toast.makeText(MainActivity.this, R.string.error_response, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
