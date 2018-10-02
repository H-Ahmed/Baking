package com.example.hesham.baking;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.hesham.baking.util.EspressoIdlingResource;
import com.example.hesham.baking.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp(){
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void scrollToPosition() {

        onView(ViewMatchers.withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.typeTextIntoFocusedView("Nutella Pie")));
    }


    @Test
    public void CheckFirstStepActivity(){

        onView(ViewMatchers.withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        onView(ViewMatchers.withId(R.id.recipe_steps_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        onView(ViewMatchers.withId(R.id.step_description_text_view))
                .check(ViewAssertions.matches(ViewMatchers.withText("Recipe Introduction")));


    }



}
