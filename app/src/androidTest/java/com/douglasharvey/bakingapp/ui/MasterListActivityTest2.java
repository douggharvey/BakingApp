package com.douglasharvey.bakingapp.ui;


import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.douglasharvey.bakingapp.R;
import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.android.exoplayer2.util.Assertions.checkNotNull;

@RunWith(AndroidJUnit4.class)
public class MasterListActivityTest2 {
    private IdlingRegistry idlingRegistry;
    private IdlingResource idlingResource;

    @Rule
    public final ActivityTestRule<MasterListActivity> masterListActivityActivityRule
            = new ActivityTestRule<>(MasterListActivity.class, true);

    @Before
    public void registerIdlingResource() {
        Timber.d("registerIdlingResource: ");
        idlingResource = OkHttp3IdlingResource.create("okhttp3",
                masterListActivityActivityRule.getActivity().getOkHttpClient());
        idlingRegistry = IdlingRegistry.getInstance();
        idlingRegistry.register(idlingResource);
    }

    @After
    public void unregisterIdlingResource() {
        Timber.d("unregisterIdlingResource: ");
        if (idlingResource != null)
            idlingRegistry.unregister(idlingResource);
    }


    @Test
    public void clickTodetail2() {
        //masterListActivityActivityRule.launchActivity(null);
    /*    try {
            // thread to sleep for 1000 milliseconds
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println(e);
        }
        */
        Timber.d("2 - clickTodetail2: ");
        onView(withId(R.id.rv_recipe_master_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.tv_title_ingredients))
                .check(matches(withText("Ingredients:")));
        Timber.d("2-clickTodetail: finishActivity");
        masterListActivityActivityRule.finishActivity();
        Timber.d("2-clickTodetail: after finishActivity");

    }

/*    @Test
    public void dummyTest() {
        onView(withId(R.id.tv_master_recipe_name))
                .check(matches(withText("Nutella Pie")));
    }
*/

/*    @Test
    public void clickItem() {
        onView(withId(R.id.rv_recipe_master_list))
                .perform(RecyclerViewActions.scrollToPosition(25))
                .check(matches(anything()));
    }
*/
  /* @Test
    public void RecyclerViewIsBeingDisplayed()  {
        Timber.d("RecyclerViewIsBeingDisplayed: ");
        onView(withId(R.id.rv_recipe_master_list)).check(matches(isDisplayed()));
    }
*/

/*
      @Test
      public void RecyclerViewTextViewsHaveCorrectText()  {
          Timber.d("RecyclerViewTextViewsHaveCorrectText: ");
          onView(withId(R.id.rv_recipe_master_list))
                  .check(matches(atPosition(1, hasDescendant(withText("Brownies")))));
        onView(withId(R.id.rv_recipe_master_list))
                .check(matches(atPosition(1, hasDescendant(withText("Servings: 8")))));
*/




/*    @Test
    public void listCount() {
        onView(Matchers.<View>instanceOf(RecyclerView.class))
                .check(new AdapterCountAssertion(4));
    }

    static class AdapterCountAssertion implements ViewAssertion {
        private final int count;

        AdapterCountAssertion(int count) {
            this.count = count;
        }

        @Override
        public void check(View view,
                          NoMatchingViewException noViewFoundException) {
            Assert.assertTrue(view instanceof RecyclerView);
            Assert.assertEquals(count,
                    ((RecyclerView) view).getAdapter().getItemCount());
        }
    }
    */

    //Read about custom matchers
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                // has no item on such position
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
