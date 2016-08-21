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

import android.view.View;
import android.widget.TextView;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;

/**
 * Custom Doppio ViewAssertions
 */
public class DoppioExtractors {

    private DoppioExtractors() {
        //no instance
    }

    /**
     * Helper method for extracting TextView text
     * @param textViewMatcher View matcher that targets desired TextView
     * @return TextView text value
     */
    public static String extractText(Matcher<View> textViewMatcher) {
        TextViewTextExtractor extractor = new TextViewTextExtractor();
        onView(textViewMatcher).check(extractor);
        return extractor.getValue();
    }

    /**
     * Allows to extract text value from TextView for manual assertion
     */
    public static class TextViewTextExtractor extends ViewManualAssertion<TextView, String> {

        public TextViewTextExtractor() {
            super(TextView.class);
        }

        @Override
        protected String extractValue(TextView typedView) {
            return typedView.getText().toString();
        }
    }
}
