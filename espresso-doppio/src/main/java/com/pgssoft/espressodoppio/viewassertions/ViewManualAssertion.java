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
package com.pgssoft.espressodoppio.viewassertions;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.view.View;

/**
 * ViewAssertion that allows to extract value from asserted view. Helpful when value is needed for
 * manual check. See {@link DoppioExtractors.TextViewTextExtractor} for sample implementation.
 */
public abstract class ViewManualAssertion<T extends View, V> implements ViewAssertion {

    private final Class<T> clazz;
    private V value;

    public ViewManualAssertion(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * After calling check on ViewInteraction this method can be called to get extracted value
     *
     * @return extracted value
     */
    public V getValue() {

        if (value == null) {
            throw new IllegalStateException("No matching views or view assertion was not called before!");
        }
        return value;
    }

    @Override
    public void check(View view, NoMatchingViewException noViewFoundException) {
        T typedView = clazz.cast(view);
        value = extractValue(typedView);
    }

    /**
     * Called internally by ViewManualAssertion
     * <p>
     * Provide implementation that will return extracted value from provided argument
     *
     * @param typedView view that provides value for extraction
     * @return extracted value
     */
    protected abstract V extractValue(T typedView);
}
