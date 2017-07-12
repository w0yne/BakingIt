package com.w0yne.android.bakingit;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.w0yne.android.bakingit.data.Recipe;
import com.w0yne.android.bakingit.data.Step;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class RecipeStepDetailActivity extends AppCompatActivity {

    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.recipe_step_detail_container)
    ViewPager mStepsViewPager;
    @BindView(R.id.button_bar)
    ButtonBarLayout mButtonBarLayout;
    @BindView(R.id.previous_btn)
    Button mPrevBtn;
    @BindView(R.id.next_btn)
    Button mNextBtn;

    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecipe = Realm.getDefaultInstance().where(Recipe.class)
                .equalTo("uid", getIntent().getIntExtra(Intents.EXTRA_RECIPE_ID, -1))
                .findFirst();

        int stepId = getIntent().getIntExtra(RecipeStepDetailFragment.ARG_STEP_ID, -1);

        int position = 0;

        Adapter adapter = new Adapter(getFragmentManager());
        List<Bundle> args = new ArrayList<>();
        for (int i = 0; i < mRecipe.stepList.size(); i++) {
            Step step = mRecipe.stepList.get(i);
            Bundle arg = new Bundle();
            arg.putInt(RecipeStepDetailFragment.ARG_STEP_ID, step.uid);
            args.add(arg);
            if (step.uid == stepId) {
                position = i;
            }
        }
        adapter.addStepFragment(args);
        mStepsViewPager.setAdapter(adapter);
        mStepsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                checkPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
        mStepsViewPager.setCurrentItem(position);
        checkPosition(position);

        mPrevBtn.setOnClickListener(v -> moveBackward());
        mNextBtn.setOnClickListener(v -> moveForward());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterLandscape();
        } else {
            enterPortrait();
        }
    }

    private void enterLandscape() {
        mAppBarLayout.setVisibility(View.GONE);
        mButtonBarLayout.setVisibility(View.GONE);
    }

    private void enterPortrait() {
        mAppBarLayout.setVisibility(View.VISIBLE);
        mButtonBarLayout.setVisibility(View.VISIBLE);
    }

    private void checkPosition(int position) {
        setTitle(mRecipe.stepList.get(position).shortDescription);
        mPrevBtn.setEnabled(position != 0);
        mNextBtn.setEnabled(position != mStepsViewPager.getAdapter().getCount() - 1);
    }

    private void moveForward() {
        mStepsViewPager.setCurrentItem(mStepsViewPager.getCurrentItem() + 1);
    }

    private void moveBackward() {
        mStepsViewPager.setCurrentItem(mStepsViewPager.getCurrentItem() - 1);
    }

    private class Adapter extends FragmentStatePagerAdapter {

        private List<Bundle> mFragmentArgs;

        public Adapter(final FragmentManager fm) {
            super(fm);
            mFragmentArgs = new ArrayList<>();
        }

        public void addStepFragment(List<Bundle> args) {
            mFragmentArgs.clear();
            mFragmentArgs.addAll(args);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(final int position) {
            return Fragment.instantiate(RecipeStepDetailActivity.this,
                    RecipeStepDetailFragment.class.getName(), mFragmentArgs.get(position));
        }

        @Override
        public int getCount() {
            return mFragmentArgs.size();
        }
    }
}
