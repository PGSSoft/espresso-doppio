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
package com.pgssoft.espressodoppio.idlingresources;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.pgssoft.espressodoppio.ComponentFinder;

import org.hamcrest.Matcher;

/**
 * IdlingResource that waits for specific Activity to be in Resumed state or in Paused state
 * <p/>
 * Helpful when test has to wait for specific screen. It can be additionally parametrized with Matcher<Activity>
 * to wait for particular instance.
 * <p/>
 */
public class ActivityIdlingResource extends BaseIdlingResource implements ComponentIdlingResource {

    private Class<? extends Activity> clazz;
    private Class<? extends Activity> otherThan;
    private Matcher<Activity> activityMatcher;
    private ComponentFinder componentFinder;

    /**
     * Constructs new ActivityIdlingResource instance
     *
     * @param clazz class of Activity that IdlingResource waits for, if null it will wait for other
     *              Activity type than current one.
     */
    public ActivityIdlingResource(@Nullable Class<? extends Activity> clazz) {
        init(clazz);
    }

    /**
     * Constructs new ActivityIdlingResource instance
     *
     * @param clazz   of Activity that IdlingResource waits for, if null it will wait for other
     *                Activity type than current one.
     * @param timeout shorcut param that allows to set global IdlingResource timeout
     */
    public ActivityIdlingResource(@Nullable Class<? extends Activity> clazz, long timeout) {
        super(timeout);
        init(clazz);
    }

    private void init(Class<? extends Activity> clazz) {

        if (clazz != null) {
            this.clazz = clazz;
        } else {
            // if clazz is not passed and there is no current activity in resumed state - let it burn NPE
            this.otherThan = componentFinder.findCurrentActivity().getClass();
        }
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        super.registerIdleTransitionCallback(callback);
        componentFinder = provideComponentFinder();
    }

    @Override
    public String getName() {
        return super.getName() + " activity: " + clazz + " other: " + otherThan;
    }

    @Override
    protected boolean isIdle() {
        Activity currentActivity = componentFinder.findCurrentActivity();
        boolean isOtherThanInitialMode = otherThan != null && currentActivity != null;
        boolean isDesiredClassMode = clazz != null && currentActivity != null;
        boolean isMatching = activityMatcher == null || activityMatcher.matches(currentActivity);

        //handle wait for other than initial class mode
        if (isOtherThanInitialMode) {
            // if currentActivity is other class in comparison to one got in init(...) than it's true
            boolean isOtherClass = !otherThan.equals(currentActivity.getClass());
            // if activityMatcher was set we need to check against it, if null just ignore
            if (isOtherClass && isMatching) {
                return true;
            }
            //handle wait for desired class
        } else if (isDesiredClassMode) {
            boolean isDesiredClass = clazz.equals(currentActivity.getClass());
            // if activityMatcher was set we need to check against it, if null just ignore
            if (isDesiredClass && isMatching) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets optional matcher for target Activity
     *
     * @param matcher Matcher<Activity>
     * @return this for chaining with {@link BaseIdlingResource#register()}
     */
    public ActivityIdlingResource setActivityMatcher(Matcher<Activity> matcher) {
        this.activityMatcher = matcher;
        return this; //chaining ftw
    }

    /**
     * Provides ComponentFinder instance, by default new one. Usually shouldn't be overriden it's
     * here mostly for testing purposes.
     *
     * @return ComponentFinder
     */
    @Override
    public ComponentFinder provideComponentFinder() {
        return new ComponentFinder();
    }
}
