/*
 * MIT License
 *
 * Copyright (c) 2016 PGS Software SA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.pgssoft.espressodoppio;

import android.app.Activity;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.util.TreeIterables;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class that allows easily extract Activities, Fragments or Views with InstrumentationRegistry
 */
public class ComponentFinder {

    @Nullable
    public Activity findCurrentActivity() {
        Iterator<Activity> allActivitiesIterator = findAllInStage(Stage.RESUMED).iterator();
        return allActivitiesIterator.hasNext() ? allActivitiesIterator.next() : null;
    }

    @Nullable
    public Activity findFirstMatching(Stage activityStage, @NonNull Matcher<Activity> matcher) {
        Collection<Activity> foundActivities = findAllInStage(activityStage);
        for (Activity activity : foundActivities) {

            if (matcher.matches(activity)) {
                return activity;
            }
        }
        return null;
    }

    @NonNull
    public List<Activity> findMatching(Stage activityStage, @NonNull Matcher<Activity> matcher) {
        Collection<Activity> foundActivities = findAllInStage(activityStage);
        List<Activity> matchingActivities = new ArrayList<>();
        for (Activity activity : foundActivities) {

            if (matcher.matches(activity)) {
                matchingActivities.add(activity);
            }
        }
        return matchingActivities;
    }

    @NonNull
    public List<Activity> findAllInStage(final Stage activityStage) {
        final List<Activity> foundActivities = new ArrayList<>();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                foundActivities.addAll(ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(activityStage));
            }
        };

        if (Looper.myLooper() != Looper.getMainLooper()) {
            InstrumentationRegistry.getInstrumentation().runOnMainSync(task);
        } else {
            task.run();
        }
        return foundActivities;
    }

    @Nullable
    public Fragment findMatchingFragment(@NonNull Matcher<Fragment> matcher) {

        Activity a = findCurrentActivity();

        if (a == null || !(a instanceof AppCompatActivity)) {
            return null;
        }
        AppCompatActivity activity = (AppCompatActivity) a;
        List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();

        if (fragments == null) {
            return null;
        }
        return matchFragments(fragments, matcher);
    }

    private Fragment matchFragments(@NonNull List<Fragment> fragments, @NonNull Matcher<Fragment> matcher) {

        if (fragments.size() == 0) {
            return null;
        }
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment f = fragments.get(i);

            if (f == null) {
                continue;
            }

            if (matcher.matches(f)) {
                return f;
            }
            List<Fragment> childFragments = f.getChildFragmentManager().getFragments();

            if (childFragments == null) {
                continue;
            }
            Fragment matchingChild = matchFragments(childFragments, matcher);

            if (matchingChild != null) {
                return matchingChild;
            }
        }
        return null;
    }

    @Nullable
    public View findMatchingView(@NonNull final Matcher<View> matcher) {

        Activity a = findCurrentActivity();
        View activityView = a != null ? a.findViewById(android.R.id.content) : null;

        if (activityView != null) {
            View target = matchView(activityView, matcher);

            if (target != null) {
                return target;
            }
        }

        if (!(a instanceof AppCompatActivity)) {
            return null;
        }
        final View[] targetView = {null};
        Matcher<Fragment> fragViewMatcher = new BaseMatcher<Fragment>() {
            @Override
            public boolean matches(Object item) {

                if (item == null || !(item instanceof Fragment)) {
                    return false;
                }
                Fragment frag = (Fragment) item;
                View fragView = frag.getView();

                if (fragView == null && frag instanceof DialogFragment) {
                    DialogFragment dialogFragment = (DialogFragment) frag;
                    fragView = dialogFragment.getDialog().findViewById(android.R.id.content);
                }

                if (fragView == null) {
                    return false;
                }
                View matchingView = matchView(fragView, matcher);

                if (matchingView != null) {
                    targetView[0] = matchingView;
                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Fragment matcher that matches internal frag. views");
            }
        };

        Fragment matchingFrag = findMatchingFragment(fragViewMatcher);
        return matchingFrag != null ? targetView[0] : null;
    }

    private View matchView(View view, Matcher<View> matcher) {

        if (view == null) {
            return null;
        }

        if (matcher.matches(view)) {
            return view;
        }

        for (View child : TreeIterables.breadthFirstViewTraversal(view)) {

            if (matcher.matches(child)) {
                return child;
            }
        }
        return null;
    }
}
