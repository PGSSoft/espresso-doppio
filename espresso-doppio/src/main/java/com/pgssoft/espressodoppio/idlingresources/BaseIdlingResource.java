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

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.TimeUnit;

/**
 * Base Doppio IdlingResource implementation extended by most of our implementations
 * <p/>
 * Default constructors allows to set global timeout but it's not recommended when more than
 * one IdlingResource registered per section.
 */
public abstract class BaseIdlingResource implements IdlingResource {

    public static long DEFAULT_IDLING_TIMEOUT = 60000;
    protected ResourceCallback resourceCallback;
    private boolean idle;

    public BaseIdlingResource() {
        IdlingPolicies.setIdlingResourceTimeout(DEFAULT_IDLING_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public BaseIdlingResource(long timeout) {
        IdlingPolicies.setIdlingResourceTimeout(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getName() {
        return getClass().isAnonymousClass() ? "Anonymous BaseIdlingResource" : getClass().getSimpleName();
    }

    @Override
    public boolean isIdleNow() {

        if (isIdle()) {
            idle = true;
        } else if (!isOneShot()) {
            idle = false;
        }

        if (idle) {
            resourceCallback.onTransitionToIdle();
        }

        return idle;
    }

    protected abstract boolean isIdle();

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    /**
     * By default BaseIdlingResource cannot be used again after going idle, it can be override by
     * changing isOneShot return value to false
     *
     * @return true if IdlingResource should stay in idle state afte switching to this state
     */
    public boolean isOneShot() {
        return true;
    }

    /**
     * Shorcut for calling {@link Espresso#registerIdlingResources(IdlingResource...)}
     * @return true if IdlingResource registered successfully
     */
    public BaseIdlingResource register() {
        Espresso.registerIdlingResources(this);
        //noinspection unchecked
        return this;
    }

    /**
     * Shortcut for calling {@link Espresso#unregisterIdlingResources(IdlingResource...)}
     * @return true if IdlingResource unregistered successfully
     */
    public boolean unregister() {
        return Espresso.unregisterIdlingResources(this);
    }
}
