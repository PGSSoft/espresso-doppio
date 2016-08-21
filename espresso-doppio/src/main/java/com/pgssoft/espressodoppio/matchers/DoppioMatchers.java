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
package com.pgssoft.espressodoppio.matchers;

import android.app.Activity;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.is;

/**
 * Custom Doppio matchers
 */
public class DoppioMatchers {

    private DoppioMatchers() {
        //no instance
    }

    //Activity matchers section:

    /**
     * Matches activity that has window focus - it means that activity is not hidden under dialog.
     * It's useful for waiting until ProgressDialog is dismissed
     */
    public static final Matcher<Activity> focusedActivityMatcher = new BoundedMatcher<Activity, Activity>(Activity.class) {

        @Override
        public void describeTo(Description description) {
            description.appendText("Matches activity that is not hidden under dialog");
        }

        @Override
        protected boolean matchesSafely(Activity item) {
            return item.hasWindowFocus();
        }
    };

    //Fragment matchers section:

    /**
     * Matches fragment with specific tag
     *
     * @param tag fragment tag
     * @return BaseMatcher<Fragment>
     */
    public static Matcher<Fragment> withTag(final String tag) {
        return new BoundedMatcher<Fragment, Fragment>(Fragment.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("Matches fragment tag");
            }

            @Override
            protected boolean matchesSafely(Fragment item) {
                String fragTag = item.getTag();
                return fragTag != null && fragTag.equals(tag);
            }
        };
    }

    /**
     * Maches fragment with specific id
     *
     * @param id fragment id
     * @return BaseMatcher<Fragment>
     */
    public static Matcher<Fragment> withId(final int id) {
        return new BoundedMatcher<Fragment, Fragment>(Fragment.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("Matches fragment id");
            }

            @Override
            protected boolean matchesSafely(Fragment item) {
                return item.getId() == id;
            }
        };
    }

    //View matchers section:

    /**
     * Matches RecyclerView that has at least one item in own adapter
     */
    public static Matcher<View> recyclerWithItems =
            new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {

                @Override
                public void describeTo(Description description) {
                    description.appendText("Matches recycler with one or more items");
                }

                @Override
                protected boolean matchesSafely(RecyclerView item) {
                    RecyclerView.Adapter adapter = item.getAdapter();

                    if (adapter != null && adapter.getItemCount() > 0) {
                        return true;
                    }

                    return false;
                }
            };

    public static Matcher<View> withHintText(final Matcher<String> hintTextMatcher) {
        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("Matches TextView with hint text: ");
                hintTextMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(TextView item) {
                return hintTextMatcher.matches(item.getHint() != null ? item.getHint().toString() : null);
            }
        };
    }

    public static Matcher<View> withHintText(String text) {
        return withHintText(is(text));
    }
}
