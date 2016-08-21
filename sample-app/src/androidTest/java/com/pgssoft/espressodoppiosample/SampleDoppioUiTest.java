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
package com.pgssoft.espressodoppiosample;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.pgssoft.espressodoppio.DoppioUtils;
import com.pgssoft.espressodoppio.idlingresources.BaseIdlingResource;
import com.pgssoft.espressodoppio.idlingresources.FragmentIdlingResource;
import com.pgssoft.espressodoppio.idlingresources.TimeoutIdlingResource;
import com.pgssoft.espressodoppio.idlingresources.ViewIdlingResource;
import com.pgssoft.espressodoppio.matchers.DoppioMatchers;
import com.pgssoft.espressodoppio.viewassertions.DoppioExtractors;
import com.pgssoft.espressodoppiosample.fragments.DetailFragment;
import com.pgssoft.espressodoppiosample.fragments.YesNoDialog;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.pgssoft.espressodoppio.DoppioUtils.waitForIdle;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

/**
 * Sample test that uses Barista features for synchronization
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class SampleDoppioUiTest {

    @Rule
    public ActivityTestRule<SplashActivity> activityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Test
    public void doExampleTestStuff() throws Exception {

        DoppioUtils.wakeUp();   //some devices like HTC M7 uses first click for 'wakeup' screen without passing event to view
        final ViewIdlingResource viewIdlingResource = new ViewIdlingResource(
                both(withId(R.id.item_recycler)).and(DoppioMatchers.recyclerWithItems));
        //waits for view, it could also wait for new activity
        //also shows waitForIdle that does IdlingResource register/unregister before and after
        //Runnable execution
        waitForIdle(viewIdlingResource, new Runnable() {
            @Override
            public void run() {
                onView(withId(R.id.item_recycler)).perform(RecyclerViewActions.actionOnItemAtPosition(3, click())); //scroll here sometimes not working, maybe bug in RecyclerViewActions
            }
        });

        //wait for particular fragment in desired container
        final BaseIdlingResource res = new FragmentIdlingResource(
                allOf(
                        DoppioMatchers.withId(R.id.fragment_list_container),
                        instanceOf(DetailFragment.class)
                )
        ).register();

        //get view value for manual assertion - it's sometimes required by test abstraction
        //extraction uses ViewAssertions implementation so it also will wait for iddle state
        String text = DoppioExtractors.extractText(withId(R.id.dialog_start_button));
        Assert.assertEquals(text, "Start dialog");

        onView(withId(R.id.dialog_start_button)).perform(click());
        res.unregister();

        //wait for fragment dialog
        final BaseIdlingResource res2 = new FragmentIdlingResource(YesNoDialog.FRAGMENT_TAG).register();
        onView(withText("Yes")).inRoot(isDialog()).perform(click());
        res2.unregister();

        //simple timeout, should be used only as last resort
        final BaseIdlingResource timeout = new TimeoutIdlingResource(6000).register();
        onView(isRoot()).perform(pressBack());
        timeout.unregister();
    }

    @NonNull
    public static ViewInteraction getRootView(@NonNull Activity activity, @IdRes int id) {
        return onView(withId(id)).inRoot(withDecorView(not(is(activity.getWindow().getDecorView()))));
    }
}