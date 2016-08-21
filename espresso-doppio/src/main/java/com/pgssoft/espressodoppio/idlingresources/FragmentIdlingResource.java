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
import android.support.test.InstrumentationRegistry;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.pgssoft.espressodoppio.ComponentFinder;
import com.pgssoft.espressodoppio.matchers.DoppioMatchers;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;

/**
 * IdlingResource that waits for matching Fragment
 */
public class FragmentIdlingResource extends BaseIdlingResource implements ComponentIdlingResource {

    private String tag = null;
    private int id;
    private Matcher<Fragment> matcher = null;
    private ComponentFinder componentFinder;

    /**
     * Creates FragmentIdlingResource that will wait for fragment with specific tag
     *
     * @param tag of matching fragment
     */
    public FragmentIdlingResource(String tag) {
        this.tag = tag;
    }

    /**
     * Creates FragmentIdlingResource that will wait for fragment with specific tag
     *
     * @param tag     of matching fragment
     * @param timeout for Espresso {@link android.support.test.espresso.IdlingPolicies#setIdlingResourceTimeout(long, TimeUnit)}
     */
    public FragmentIdlingResource(String tag, long timeout) {
        super(timeout);
        this.tag = tag;
    }

    /**
     * Creates FragmentIdlingResource that will wait for fragment with specific id
     *
     * @param id of matching fragment
     */
    public FragmentIdlingResource(int id) {
        this.id = id;
    }

    /**
     * Creates FragmentIdlingResource that will wait for fragment with specific id
     *
     * @param id      of matching fragment
     * @param timeout for Espresso {@link android.support.test.espresso.IdlingPolicies#setIdlingResourceTimeout(long, TimeUnit)}
     */
    public FragmentIdlingResource(int id, long timeout) {
        super(timeout);
        this.id = id;
    }

    /**
     * Creates FragmentIdlingResource that will wait for matching fragment
     *
     * @param matcher for fragment
     */
    public FragmentIdlingResource(Matcher<Fragment> matcher) {
        this.matcher = matcher;
    }

    /**
     * Creates FragmentIdlingResource that will wait for matching fragment
     *
     * @param matcher for fragment
     * @param timeout for Espresso {@link android.support.test.espresso.IdlingPolicies#setIdlingResourceTimeout(long, TimeUnit)}
     */
    public FragmentIdlingResource(Matcher<Fragment> matcher, long timeout) {
        super(timeout);
        this.matcher = matcher;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        super.registerIdleTransitionCallback(callback);
        componentFinder = provideComponentFinder();
    }

    @Override
    public String getName() {
        return super.getName() + " matcher: " + matcher + ", tag: " + tag + ", id: "
                + (id > 0 ? InstrumentationRegistry.getTargetContext().getResources().getResourceEntryName(id) : null);
    }

    @Override
    protected boolean isIdle() {

        Activity activity = componentFinder.findCurrentActivity();

        if (activity instanceof AppCompatActivity) {

            if (matcher != null) {
                return componentFinder.findMatchingFragment(matcher) != null;
            }
            if (tag != null) {
                return componentFinder.findMatchingFragment(DoppioMatchers.withTag(tag)) != null;
            } else {
                return componentFinder.findMatchingFragment(DoppioMatchers.withId(id)) != null;
            }
        }

        return true;
    }

    @Override
    public ComponentFinder provideComponentFinder() {
        return new ComponentFinder();
    }
}
