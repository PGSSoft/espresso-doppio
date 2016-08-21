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
package com.pgssoft.espressodoppio.viewactions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

/**
 * Custom Doppio ViewActions
 */
public class DoppioActions {

    private DoppioActions() {
        //no instance
    }

    /**
     * Action that does nothing
     * <p/>
     * Useful for waiting for idle sync. In some cases waiting for IdlingResource to be idle is
     * crucial for some sync code execution, for example assertions.
     * <p/>
     * Usage:
     * //register desired IdlingResource
     * onView(withText("Yes")).perform(DoppioActions.noAction()); //espresso will stay here until idle
     *
     * @return new ViewAction that does nothing
     */
    public static ViewAction noAction() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return Matchers.any(View.class);
            }

            @Override
            public String getDescription() {
                return "Performs no action, just to satisfy idling resources";
            }

            @Override
            public void perform(UiController uiController, View view) {

            }
        };
    }
}
