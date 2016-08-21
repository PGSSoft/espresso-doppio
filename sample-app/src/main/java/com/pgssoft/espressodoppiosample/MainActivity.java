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
package com.pgssoft.espressodoppiosample;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pgssoft.espressodoppiosample.fragments.DetailFragment;
import com.pgssoft.espressodoppiosample.fragments.ListFragment;
import com.pgssoft.espressodoppiosample.models.ListItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ListFragment.OnItemClickListener {

    private static final String RECYCLER_STATE = "RecyclerState";
    private static final String PREV_DATA = "PrevData";
    private static final String DETAIL_PORTRAIT_TAG = "detail_portrait_tag";
    private static final String SELECTED_DETAIL = "SelectedDetail";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private ListFragment listFragment;
    private DetailFragment detailFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<ListItem> prevData = null;
        Parcelable prevState = null;
        ListItem prevDetailItem = null;

        if (savedInstanceState != null) {
            prevData = savedInstanceState.getParcelableArrayList(PREV_DATA);
            prevState = savedInstanceState.getParcelable(RECYCLER_STATE);
            prevDetailItem = savedInstanceState.getParcelable(SELECTED_DETAIL);
        }

        listFragment = ListFragment.newInstance(prevData, prevState);
        listFragment.setOnItemClickListener(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_list_container, listFragment);

        View secondColumn = ButterKnife.findById(this, R.id.fragment_detail_container);
        if (secondColumn != null) {
            detailFragment = DetailFragment.newInstance(prevDetailItem);
            transaction.replace(R.id.fragment_detail_container, detailFragment);
        }
        transaction.commit();

        setSupportActionBar(toolbar);
        configureNavigationDrawer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_STATE, listFragment.getLayoutState());
        outState.putParcelableArrayList(PREV_DATA, listFragment.getCurrentData());

        if (detailFragment == null) {
            DetailFragment frag = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAIL_PORTRAIT_TAG);

            if (frag != null) {
                outState.putParcelable(SELECTED_DETAIL, frag.getContent());
            }
        }
    }

    private void configureNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @OnClick(R.id.fab)
    protected void onFabClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //handle selected id

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void itemClicked(int position, ListItem item) {

        if (detailFragment != null) {
            detailFragment.updateContent(item);
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            DetailFragment fragment = DetailFragment.newInstance(item);
            transaction.replace(R.id.fragment_list_container, fragment, DETAIL_PORTRAIT_TAG);
            transaction.addToBackStack(DetailFragment.class.getSimpleName());
            transaction.commit();
        }
    }
}
