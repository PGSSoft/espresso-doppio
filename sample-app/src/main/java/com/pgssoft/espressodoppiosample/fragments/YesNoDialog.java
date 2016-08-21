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
package com.pgssoft.espressodoppiosample.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;


public class YesNoDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String FRAGMENT_TAG = "yesNoDialog";
    private static final String TITLE_TEXT = "titleText";
    private static final String POSITIVE_TEXT = "positiveText";
    private static final String NEGATIVE_TEXT = "negativeText";
    protected YesNoActionListener listener;
    private String titleText;
    private String positiveText;
    private String negativeText;

    public static YesNoDialog newInstance(String titleText, String positiveText, String negativeText) {

        Bundle args = new Bundle();
        args.putString(TITLE_TEXT, titleText);
        args.putString(POSITIVE_TEXT, positiveText);
        args.putString(NEGATIVE_TEXT, negativeText);

        YesNoDialog fragment = new YesNoDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public static void showDialog(FragmentManager fragmentManager, String title, YesNoActionListener listener) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(FRAGMENT_TAG);

        if (prev != null) {
            transaction.remove(prev);
        }
        transaction.addToBackStack(null);
        YesNoDialog fragment = YesNoDialog.newInstance(title, "Yes", "No");
        fragment.listener = listener;
        fragment.show(transaction, FRAGMENT_TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        titleText = args.getString(TITLE_TEXT);
        positiveText = args.getString(POSITIVE_TEXT);
        negativeText = args.getString(NEGATIVE_TEXT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //just placeholder implementation, i'm lazy today
        return new AlertDialog.Builder(getActivity())
                .setTitle(titleText)
                .setPositiveButton(positiveText, this)
                .setNegativeButton(negativeText, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                listener.onPositiveClick();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                listener.onNegativeClick();
                break;
        }
    }

    public interface YesNoActionListener {
        void onPositiveClick();

        void onNegativeClick();
    }
}
