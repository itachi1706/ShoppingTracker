package com.itachi1706.shoppingtracker;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.itachi1706.shoppingtracker.AsyncTasks.AppUpdateChecker;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton fab;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(this.toolbar);

        this.viewPager = (ViewPager) findViewById(R.id.activity_viewpager);
        setupViewPager(this.viewPager);

        //TODO: Bulk Uncomment this when Design Library is fixed (Refer below for issue)
        /*this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                Fragment currentFrag = adapter.getItem(viewPager.getCurrentItem());
                if (currentFrag instanceof MainActivityFragment){
                    MainActivityFragment main = (MainActivityFragment) currentFrag;
                    main.onRefresh();
                } else if (currentFrag instanceof CartFragment){
                    CartFragment cart = (CartFragment) currentFrag;
                    cart.onRefresh();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
*/

        this.tabLayout = (TabLayout) findViewById(R.id.activity_tablayout);
        this.tabLayout.setupWithViewPager(this.viewPager);

        this.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //TODO: Workaround for bug caused by Support Design Library 23. Remove next 3 lines when fix tested
        //Link to Bug: https://code.google.com/p/android/issues/detail?id=183123
        this.viewPager.clearOnPageChangeListeners();
        this.viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));

        this.fab = (FloatingActionButton) findViewById(R.id.activity_fab);
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFAB();
            }
        });

        this.coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_coordinator_layout);

        //Async Tasks here
        new AppUpdateChecker(this, PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()), true).execute();

    }

    private void clickFAB()
    {
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        Fragment currentFrag = adapter.getItem(viewPager.getCurrentItem());
        if (currentFrag instanceof MainActivityFragment)
        {
            //Main Fragment
            Snackbar.make(coordinatorLayout, "Main Fragment", Snackbar.LENGTH_SHORT)
                    .setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        } else if (currentFrag instanceof CartFragment)
        {
            //Cart Fragment
            Snackbar.make(coordinatorLayout, "Cart Fragment", Snackbar.LENGTH_SHORT)
                    .setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .show();
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new MainActivityFragment(), "Main");
        adapter.addFrag(new CartFragment(), "Cart");

        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            startActivity(new Intent(this, MainPreferences.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //TODO: Remove this class when support library is fixed
    private class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {

        private final WeakReference<TabLayout> mTabLayoutRef;
        private int mPreviousScrollState;
        private int mScrollState;

        public TabLayoutOnPageChangeListener(TabLayout tabLayout) {
            mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mPreviousScrollState = mScrollState;
            mScrollState = state;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null) {
                final boolean updateText = (mScrollState == ViewPager.SCROLL_STATE_DRAGGING)
                        || (mScrollState == ViewPager.SCROLL_STATE_SETTLING
                        && mPreviousScrollState == ViewPager.SCROLL_STATE_DRAGGING);
                tabLayout.setScrollPosition(position, positionOffset, updateText);
            }
        }

        @Override
        public void onPageSelected(int position) {
            final TabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null) {
                //noinspection ConstantConditions
                tabLayout.getTabAt(position).select();
            }

            ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
            Fragment currentFrag = adapter.getItem(viewPager.getCurrentItem());
            if (currentFrag instanceof MainActivityFragment){
                MainActivityFragment main = (MainActivityFragment) currentFrag;
                main.onRefresh();
            } else if (currentFrag instanceof CartFragment){
                CartFragment cart = (CartFragment) currentFrag;
                cart.onRefresh();
            }
        }
    }
}
