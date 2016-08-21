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
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pgssoft.espressodoppiosample.App;
import com.pgssoft.espressodoppiosample.R;
import com.pgssoft.espressodoppiosample.data.DataProvider;
import com.pgssoft.espressodoppiosample.models.ListItem;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class ListFragment extends Fragment {

    private static final String prevItemsArg = "PreviousItems";
    private static final String prevLayoutStateArg = "PreviousLayoutState";

    private ItemAdapter adapter = new ItemAdapter();
    private DataProvider dataProvider = App.getInstance().getDataProvider();
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private RecyclerView recycler;
    private Parcelable prevLayoutState;
    private ProgressDialog dialog;

    public static ListFragment newInstance(ArrayList<ListItem> savedItems, Parcelable layoutState) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();

        if (savedItems != null && layoutState != null) {
            args.putParcelableArrayList(prevItemsArg, savedItems);
            args.putParcelable(prevLayoutStateArg, layoutState);
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle args = getArguments();

        if (args != null) {
            ArrayList<ListItem> previousItems = args.getParcelableArrayList(prevItemsArg);

            if (previousItems != null) {
                adapter.items.addAll(previousItems);
            }
            prevLayoutState = args.getParcelable(prevLayoutStateArg);
        }

        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = (RecyclerView) view;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        recycler.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .build()
        );

        if (prevLayoutState != null) {
            layoutManager.onRestoreInstanceState(prevLayoutState);
        }

        dialog = new ProgressDialog(getContext());
        dialog.setIndeterminate(true);
        dialog.setTitle(getString(R.string.progress_loading));
        dialog.show();

        subscriptions.add(dataProvider.getAllItems(adapter.items)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ListItem>>() {
                    @Override
                    public void onCompleted() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNext(List<ListItem> listItems) {
                        adapter.items.addAll(listItems);
                        adapter.notifyDataSetChanged();
                    }
                }));
    }

    public Parcelable getLayoutState() {
        return recycler.getLayoutManager().onSaveInstanceState();
    }

    public ArrayList<ListItem> getCurrentData() {
        return adapter.items;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        adapter.onItemClickListener = listener;
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public interface OnItemClickListener {
        void itemClicked(int position, ListItem item);
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView primaryText;
        TextView secondaryText;
        View itemView;

        ItemViewHolder(View itemView) {
            super(itemView);
            primaryText = (TextView) itemView.findViewById(R.id.item_text);
            secondaryText = (TextView) itemView.findViewById(R.id.item_text_secondary);
            this.itemView = itemView;
        }
    }

    private static class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        ArrayList<ListItem> items = new ArrayList<>();
        OnItemClickListener onItemClickListener = null;

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View inflatedView = inflater.inflate(R.layout.list_item, parent, false);
            return new ItemViewHolder(inflatedView);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            final int adapterPos = holder.getAdapterPosition();
            final ListItem item = items.get(adapterPos);
            holder.primaryText.setText(item.getName());
            holder.secondaryText.setText(App.getInstance().getString(R.string.dummy_text));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (onItemClickListener != null) {
                        onItemClickListener.itemClicked(adapterPos, item);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public int getItemPosition(ListItem prevSelected) {
            if (prevSelected == null) return 0;

            for (int i = 0; i < items.size(); i++) {
                if (prevSelected.equals(items.get(i))) {
                    return i;
                }
            }

            return 0;
        }
    }
}
