package com.w0yne.android.bakingit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import static io.appflate.restmock.utils.RequestMatchers.pathEndsWith;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.w0yne.android.bakingit.net.NetworkServiceGenerator;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.appflate.restmock.RESTMockServer;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> mRule = new IntentsTestRule<MainActivity>(MainActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            NetworkServiceGenerator.setBaseUrl(RESTMockServer.getUrl());
            RESTMockServer.whenGET(pathEndsWith("android-baking-app-json"))
                    .delay(TimeUnit.SECONDS, 1)
                    .thenReturnFile("baking.json");
        }
    };

    @Before
    public void mockServerAndIntent() {
        intending(isInternal()).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));
    }

    @Test
    public void mainUiTest() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        checkItem("Nutella Pie", 1);
        checkItem("Brownies", 2);
        checkItem("Yellow Cake", 3);
        checkItem("Cheesecake", 4);
    }

    @AfterClass
    public static void tearDown() {
        RESTMockServer.reset();
    }

    private void checkItem(String name, int id) {
        ViewInteraction interaction = onView(withText(name));
        interaction.check(matches(isDisplayed()));

        interaction.perform(click());
        intended(allOf(
                hasExtra(Intents.EXTRA_RECIPE_ID, id),
                hasComponent(hasClassName(RecipeStepListActivity.class.getName()))));
    }
}