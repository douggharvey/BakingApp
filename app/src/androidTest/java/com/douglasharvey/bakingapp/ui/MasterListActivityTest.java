package com.douglasharvey.bakingapp.ui;


import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.douglasharvey.bakingapp.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MasterListActivityTest {
    private IdlingRegistry idlingRegistry;
    private IdlingResource idlingResource;

    @Rule
    public final ActivityTestRule<MasterListActivity> masterListActivityActivityRule
            = new ActivityTestRule<>(MasterListActivity.class, true);

    @Before
    public void registerIdlingResource() {
        Timber.d("registerIdlingResource: ");
        idlingResource = masterListActivityActivityRule.getActivity().getIdlingResource();
        idlingRegistry = IdlingRegistry.getInstance();
        idlingRegistry.register(idlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        Timber.d("unregisterIdlingResource: ");
        if (idlingResource != null)
            idlingRegistry.unregister(idlingResource);
    }

    private void pause() {
        //Note: Even though I followed the same approach as in TeaTime Exercise 4, as per the below to-do item,
        // my tests frequently failed due to late registration of the idling resource therefore I introduced a pause of 1 second here.

        /*  (4) Using the method you created, get the IdlingResource variable.
         * Then call downloadImage from ImageDownloader. To ensure there's enough time for IdlingResource
         * to be initialized, remember to call downloadImage in either onStart or onResume.
         * This is because @Before in Espresso Tests is executed after the activity is created in
         * onCreate, so there might not be enough time to register the IdlingResource if the download is
         * done too early.
         *
         * */
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @Test
    public void mainViewPopulates() {
        pause();

        ViewInteraction textView = onView(
                Matchers.allOf(withId(R.id.tv_master_recipe_name), withText("Nutella Pie"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.rv_recipe_master_list),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("Nutella Pie")));
        masterListActivityActivityRule.finishActivity();
    }

    @Test
    public void clickToDetail() {
        pause();

        onView(withId(R.id.rv_recipe_master_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.tv_title_ingredients))
                .check(matches(withText("Ingredients:")));
        masterListActivityActivityRule.finishActivity();
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
