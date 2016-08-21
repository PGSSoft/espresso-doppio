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

import android.view.View;

import com.pgssoft.espressodoppio.ComponentFinder;

import org.hamcrest.Matcher;

/**
 * IdlingResource that waits for matching view in view hierarchy.
 */
public class ViewIdlingResource extends BaseIdlingResource implements ComponentIdlingResource {

    private Matcher<View> viewMatcher;
    private ComponentFinder componentFinder;

    public ViewIdlingResource(Matcher<View> viewMatcher) {
        this.viewMatcher = viewMatcher;
    }

    public ViewIdlingResource(Matcher<View> viewMatcher, long timeout) {
        super(timeout);
        this.viewMatcher = viewMatcher;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        super.registerIdleTransitionCallback(callback);
        this.componentFinder = provideComponentFinder();
    }

    @Override
    public String getName() {
        return super.getName() + " matcher: " + viewMatcher;
    }

    @Override
    protected boolean isIdle() {
        View view = componentFinder.findMatchingView(viewMatcher);
        return view != null;
    }

    @Override
    public ComponentFinder provideComponentFinder() {
        return new ComponentFinder();
    }
}
