package com.itachi1706.shoppingtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.itachi1706.shoppingtracker.Adapters.HistoryItemRecyclerAdapter;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.HistoryItem;
import com.itachi1706.shoppingtracker.utility.HistoryObjectHelper;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.text.DecimalFormat;

public class ViewHistoryItemActivity extends AppCompatActivity {

    String jsonString;

    RecyclerView recyclerView;
    Toolbar toolbar;

    HistoryItemRecyclerAdapter adapter;
    HistoryItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_history_item);

        this.toolbar = (Toolbar) findViewById(R.id.activity_history_toolbar);
        setSupportActionBar(this.toolbar);

        if (!(this.getIntent().hasExtra("itemJSON"))){
            errorExit();
        }

        jsonString = this.getIntent().getStringExtra("itemJSON");
        item = HistoryObjectHelper.getHistoryItemObjectFromJson(jsonString);

        DecimalFormat df = new DecimalFormat("0.00");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Total: $" + df.format(item.getTotal()));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.recyclerView = (RecyclerView) findViewById(R.id.rv_history_item);

        //RecyclerView Init
        this.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Resume
        CartItem[] cartItems = item.getCart();
        adapter = new HistoryItemRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        for (CartItem item : cartItems){
            adapter.addItem(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_history_item, menu);
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
        } else if (id == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void errorExit(){
        //ERROR
        Log.e(StaticReferences.TAG, "Not supposed to be here! Exiting");
        new AlertDialog.Builder(this).setTitle("ERROR")
                .setMessage("Unable to parse JSON")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewHistoryItemActivity.this.finish();
                    }
                }).show();
    }
}
