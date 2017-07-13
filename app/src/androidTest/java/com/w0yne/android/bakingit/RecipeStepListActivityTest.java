package com.w0yne.android.bakingit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import static io.appflate.restmock.utils.RequestMatchers.pathEndsWith;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.w0yne.android.bakingit.data.Recipe;
import com.w0yne.android.bakingit.data.Step;
import com.w0yne.android.bakingit.net.ApiService;
import com.w0yne.android.bakingit.net.NetworkServiceGenerator;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import io.appflate.restmock.RESTMockServer;
import io.realm.Realm;

@RunWith(AndroidJUnit4.class)
public class RecipeStepListActivityTest {

    private Recipe mRecipe;

    @Rule
    public IntentsTestRule<RecipeStepListActivity> mRule =
            new IntentsTestRule<RecipeStepListActivity>(RecipeStepListActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Intent intent = super.getActivityIntent();
                    intent.putExtra(Intents.EXTRA_RECIPE_ID, 1);
                    return intent;
                }

                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    RESTMockServer.whenGET(pathEndsWith("android-baking-app-json"))
                            .thenReturnFile("baking.json");
                    fetchData();
                }
            };

    @Before
    public void prepData() {
        Realm realm = Realm.getDefaultInstance();
        mRecipe = realm.copyFromRealm(
                realm.where(Recipe.class).equalTo("uid", 1).findFirst());
        intending(isInternal()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));
    }

    @Test
    public void testRecipeStepList() {
        onView(new RecyclerViewMatcher(R.id.recipe_step_list).atPosition(0))
                .check(matches(hasDescendant(withText(mRecipe.getDisplayIngredients()))));


        for (int i = 0; i < mRecipe.getStepList().size(); i++) {
            Step step = mRecipe.getStepList().get(i);

            onView(withId(R.id.recipe_step_list)).perform(scrollToPosition(i + 1));

            ViewInteraction interaction =
                    onView(new RecyclerViewMatcher(R.id.recipe_step_list).atPosition(i + 1))
                            .check(matches(hasDescendant(withText(step.getShortDescription()))));

            if (!Utility.isTablet(InstrumentationRegistry.getTargetContext())) {
                interaction.perform(click());
                intended(allOf(
                        hasExtra(Intents.EXTRA_RECIPE_ID, mRecipe.getUid()),
                        hasExtra(RecipeStepDetailFragment.ARG_STEP_ID, step.getUid()),
                        hasComponent(hasClassName(RecipeStepDetailActivity.class.getName()))));
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        RESTMockServer.reset();
    }

    private void fetchData() {
        ApiService apiService =
                NetworkServiceGenerator.createService(ApiService.class, RESTMockServer.getUrl());
        try {
            List<Recipe> recipes = apiService.getRecipes().execute().body();
            Realm.getDefaultInstance().executeTransaction(realm -> {
                assert recipes != null;
                for (Recipe recipe : recipes) {
                    recipe.normalizedData();
                }
                realm.insertOrUpdate(recipes);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}