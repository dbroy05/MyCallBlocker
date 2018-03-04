package com.diby.mycallblocker;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.diby.mycallblocker.activity.HomeActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Testing the HomeActivity UI components
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeActivityTest {

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule<>(
            HomeActivity.class);

    @Test
    public void checkAllComponentsLoaded() {
        //Check view pager is loaded
        onView(withId(R.id.viewpager)).check(matches(isDisplayed()));

        //Check tabs is loaded
        onView(withId(R.id.tabs)).check(matches(isDisplayed()));

    }

}
