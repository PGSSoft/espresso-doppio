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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pgssoft.espressodoppiosample.R;
import com.pgssoft.espressodoppiosample.models.ListItem;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.GONE;


public class DetailFragment extends Fragment {

    public static final String ITEM_EXTRA = "ItemExtra";
    private final Handler handler = new Handler();
    @BindView(R.id.text_detail)
    TextView textDetail;
    @BindView(R.id.text_info_select)
    TextView textInfo;
    @BindView(R.id.dialog_start_button)
    Button startDialogButton;
    @BindView(R.id.dialog_start_button_2nd)
    Button startDialog2ndButton;
    private ListItem content;
    private Unbinder unbinder;
    private Runnable delayedYesNoRunnable;

    public static DetailFragment newInstance(ListItem item) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ITEM_EXTRA, item);
        detailFragment.setArguments(args);
        return detailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        ListItem item = getArguments() != null ? (ListItem) getArguments().getParcelable(ITEM_EXTRA) : null;
        if (item != null) {
            updateContent(item);
        }

        return view;
    }

    public ListItem getContent() {
        return content;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        handler.removeCallbacks(delayedYesNoRunnable);
    }

    @OnClick(R.id.dialog_start_button)
    public void onStartDialogButtonClick() {

        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.setTitle(R.string.loading_stuff);
        progress.show();

        Random r = new Random();
        final long delay = r.nextInt(2001) + 2000; //2s - 4s of duration

        delayedYesNoRunnable = new Runnable() {
            @Override
            public void run() {
                progress.dismiss();
                YesNoDialog.showDialog(getChildFragmentManager(), "Just showed dialog!", new YesNoDialog.YesNoActionListener() {
                    @Override
                    public void onPositiveClick() {
                        textDetail.setText("Text changed after " + delay + " delay");
                    }

                    @Override
                    public void onNegativeClick() {
                        //do nothing
                    }
                });
            }
        };
        handler.postDelayed(delayedYesNoRunnable, delay);
    }

    public void updateContent(ListItem item) {
        this.content = item;
        textDetail.setText(content.getName());

        textInfo.setVisibility(GONE);
        startDialogButton.setVisibility(View.VISIBLE);
        startDialog2ndButton.setVisibility(View.VISIBLE);
    }
}
