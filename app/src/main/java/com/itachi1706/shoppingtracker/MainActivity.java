package com.itachi1706.shoppingtracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.barcode.Barcode;
import com.itachi1706.shoppingtracker.AsyncTasks.AppUpdateChecker;
import com.itachi1706.shoppingtracker.Database.ListDB;
import com.itachi1706.shoppingtracker.FallbackBarcodeScanner.IntentIntegrator;
import com.itachi1706.shoppingtracker.FallbackBarcodeScanner.IntentResult;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.LegacyBarcode;
import com.itachi1706.shoppingtracker.Objects.ListItem;
import com.itachi1706.shoppingtracker.VisionAPI.VisionApiBarcodeCameraActivity;
import com.itachi1706.shoppingtracker.utility.CartJsonHelper;
import com.itachi1706.shoppingtracker.utility.HistoryObjectHelper;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.lang.ref.WeakReference;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton fab;
    CoordinatorLayout coordinatorLayout;

    private static int VISION_REQUEST_CODE = 100;
    private static int ADD_ITEM_REQUEST_CODE = 101;
    private static boolean isLegacyBarcode = false;

    private ListDB db;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        StaticReferences.APP_CONTEXT = getApplicationContext();

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
                } else if (currentFrag instanceof HistoryFragment){
                    HistoryFragment history = (HistoryFragment) currentFrag;
                    history.onRefresh();
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

        this.db = new ListDB(this.getApplicationContext());
        this.sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

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
            startActivityForResult(new Intent(this, AddItemToDBActivity.class), ADD_ITEM_REQUEST_CODE);
        } else if (currentFrag instanceof CartFragment)
        {
            //Cart Fragment (Scan barcode)
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            if (sp.getBoolean("vision_api_use", true)) {
                int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getApplicationContext());
                if (code == ConnectionResult.SUCCESS) {
                    Intent intent = new Intent(this, VisionApiBarcodeCameraActivity.class);
                    startActivityForResult(intent, VISION_REQUEST_CODE);
                } else {
                    //Fallback to legacy method
                    fallbackToOldBarcodeHandling();
                }
            } else {
                fallbackToOldBarcodeHandling();
            }
        } else if (currentFrag instanceof HistoryFragment){
            //TODO: Remove this else statement when FAB is removed from the fragment
            Snackbar.make(coordinatorLayout, "To Remove this FAB", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void fallbackToOldBarcodeHandling(){
        isLegacyBarcode = true;
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new MainActivityFragment(), "Main");
        adapter.addFrag(new CartFragment(), "Cart");

        //Check if there's history items, if so, add the fragment
        if (HistoryObjectHelper.getHistoryFileListSize(this) > 0){
            adapter.addFrag(new HistoryFragment(), "History (WIP)");
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(StaticReferences.TAG, "Main Activity receive request code: " + requestCode);
        if (requestCode == ADD_ITEM_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
                final Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
                if (fragment instanceof MainActivityFragment){
                    Snackbar.make(coordinatorLayout, "Item Added Successfully", Snackbar.LENGTH_SHORT).show();
                    MainActivityFragment mainActivityFragment = (MainActivityFragment) fragment;
                    mainActivityFragment.onRefresh();
                } else if (fragment instanceof CartFragment) {
                    processQuantityAndBarcodePrompt(data);
                }
            }
        }

        if (!isLegacyBarcode) {
            //Check the request
            if (requestCode == VISION_REQUEST_CODE) {
                //Check if successful
                if (resultCode == RESULT_OK) {
                    //Handle result
                    Barcode barcode = StaticReferences.barcode;
                    StaticReferences.barcode = null;
                    handleBarcode(barcode, null, false);
                    Log.i(StaticReferences.TAG, "Barcode Found: " + barcode.rawValue);
                }
            }
        } else {
            Log.d(StaticReferences.TAG, "Parsing Legacy Barcode data");
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null){
                Log.d(StaticReferences.TAG, "Found valid barcode data");

                LegacyBarcode barcode = new LegacyBarcode(result.getFormatName(), result.getContents());
                barcode.setToString(result.toString());
                handleBarcode(null, barcode, true);
                Log.i(StaticReferences.TAG, "Barcode Found: " + barcode.contents);
                Log.d(StaticReferences.TAG, "Parse Completed");
            }
        }

        Log.d(StaticReferences.TAG, "Main Activity cannot understand request code, passing to superclass");
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleBarcode(Barcode barcode, LegacyBarcode legacyBarcode, boolean isLegacyBarcode){
        ListItem item;
        if (isLegacyBarcode){
            //Use Legacy barcode
            Log.d(StaticReferences.TAG, "Handling Legacy Barcode...");
            item = getItemByBarcode(legacyBarcode.contents);

            Intent intent = new Intent(this, AddItemToDBActivity.class);
            intent.putExtra("barcode_string", legacyBarcode.contents);

            if (item == null)
                startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
            else {
                Intent sendIntent = new Intent();
                sendIntent.putExtra("itemID", item.getId());
                processQuantityAndBarcodePrompt(sendIntent);
            }
        } else {
            //Use Barcode
            Log.d(StaticReferences.TAG, "Handling GPS Vision Barcode...");
            item = getItemByBarcode(barcode.rawValue);

            Intent intent = new Intent(this, AddItemToDBActivity.class);
            intent.putExtra("barcode_string", barcode.rawValue);

            if (item == null)
                startActivityForResult(intent, ADD_ITEM_REQUEST_CODE);
            else {
                Intent sendIntent = new Intent();
                sendIntent.putExtra("itemID", item.getId());
                processQuantityAndBarcodePrompt(sendIntent);
            }
        }
    }

    private void processQuantityAndBarcodePrompt(Intent data){
        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        final Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
        if (!(fragment instanceof CartFragment))
            return;

        final CartFragment cartFragment = (CartFragment) fragment;
        long itemID = -1;
        if (data.hasExtra("itemID")){
            itemID = data.getLongExtra("itemID", -1);
        }
        if (itemID == -1) return;

        final ListItem item = db.getItemFromId(itemID);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(item.getName());
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_add_item_to_cart, null);

        final EditText qty = (EditText) view.findViewById(R.id.dialog_quantity);
        final EditText price = (EditText) view.findViewById(R.id.dialog_price);
        builder.setView(view);
        builder.setPositiveButton("Add to Cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantity = qty.getText().toString();
                String itemPrice = price.getText().toString();
                quantity = quantity.trim();
                itemPrice = itemPrice.trim();
                if (quantity.equals("") || itemPrice.equals("")) {
                    Toast.makeText(MainActivity.this, "You need to enter a price and quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                final CartItem cartItem = new CartItem(item.getId(), Integer.parseInt(qty.getText().toString()), Double.parseDouble(price.getText().toString()), item);
                CartJsonHelper.addCartItemToJsonCart(cartItem, sp);
                cartFragment.onRefresh();
                Toast.makeText(MainActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNeutralButton(android.R.string.cancel, null);
        builder.show();
    }

    private ListItem getItemByBarcode(String barcode){
        String trimmedBarcode = barcode.trim();
        return db.getItemByBarcode(trimmedBarcode);
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
                StaticReferences.isMainSwiped = true;
                main.onSwipeRefresh();
            } else if (currentFrag instanceof CartFragment){
                CartFragment cart = (CartFragment) currentFrag;
                cart.onSwipeRefresh();
            } else if (currentFrag instanceof HistoryFragment){
                HistoryFragment history = (HistoryFragment) currentFrag;
                history.onSwipeRefresh();
            }
        }
    }
}
