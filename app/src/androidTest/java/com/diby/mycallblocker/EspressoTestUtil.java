package com.diby.mycallblocker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Utility methods for espresso tests.
 */
public class EspressoTestUtil {
    /**
     * Disables progress bar animations for the views of the given activity rule
     *
     * @param activityTestRule The activity rule whose views will be checked
     */
    public static void disableProgressBarAnimations(
            ActivityTestRule<? extends FragmentActivity> activityTestRule) {
        activityTestRule.getActivity().getSupportFragmentManager()
                .registerFragmentLifecycleCallbacks(
                new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v,
                                                      Bundle savedInstanceState) {
                        // traverse all views, if any is a progress bar, replace its animation
                        traverseViews(v);
                    }
                }, true);
    }

    private static void traverseViews(View view) {
        if (view instanceof ViewGroup) {
            traverseViewGroup((ViewGroup) view);
        } else {
            if (view instanceof ProgressBar) {
                disableProgressBarAnimation((ProgressBar) view);
            }
        }
    }

    private static void traverseViewGroup(ViewGroup view) {
        final int count = view.getChildCount();
        for (int i = 0; i < count; i++) {
            traverseViews(view.getChildAt(i));
        }
    }

    /**
     * necessary to run tests on older API levels where progress bar uses handler loop to animate.
     *
     * @param progressBar The progress bar whose animation will be swapped with a drawable
     */
    private static void disableProgressBarAnimation(ProgressBar progressBar) {
        progressBar.setIndeterminateDrawable(new ColorDrawable(Color.BLUE));
    }
}
