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

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.view.Window;
import android.view.WindowManager;

import com.pgssoft.espressodoppio.idlingresources.BaseIdlingResource;


/**
 * Doppio utility methods
 */
public final class DoppioUtils {

    /**
     * Turns screen fully on
     * <p/>
     * Some devices dims screen after some time of inactivity and after switching to this state first
     * click action will be used only for waking up device.
     * <p/>
     * It's essential to wake up device before running test code.
     */
    public static void wakeUp() {

        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                //if there is no activity we should definitely crash
                //noinspection ConstantConditions
                Window window = new ComponentFinder().findCurrentActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
    }

    /**
     * Utility method that does IdlingResource registration/unregistration before and after Runnable
     * execution
     *
     * @param idlingResource
     * @param runnable
     */
    public static void waitForIdle(@NonNull BaseIdlingResource idlingResource, @NonNull Runnable runnable) {
        idlingResource.register();
        runnable.run();
        idlingResource.unregister();
    }

    //todo: check animation settings
}
