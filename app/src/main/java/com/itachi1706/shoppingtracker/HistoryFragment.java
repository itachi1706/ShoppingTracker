package com.itachi1706.shoppingtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.itachi1706.shoppingtracker.Adapters.HistoryListRecyclerAdapter;
import com.itachi1706.shoppingtracker.Adapters.StringRecyclerAdapter;
import com.itachi1706.shoppingtracker.Interfaces.OnRefreshListener;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.HistoryItem;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.Objects.ListItem;
import com.itachi1706.shoppingtracker.utility.HistoryObjectHelper;
import com.itachi1706.shoppingtracker.utility.StaticReferences;
import com.itachi1706.shoppingtracker.utility.ToastHelper;

import java.util.List;

public class HistoryFragment extends Fragment implements OnRefreshListener {

    public HistoryFragment() { }

    View v;
    SharedPreferences sp;

    //From Main Activity
    CoordinatorLayout layout;
    CardView alwaysHideThisView;
    FloatingActionButton fab;

    //From Fragment
    RecyclerView recyclerView;

    //Has Items
    HistoryListRecyclerAdapter adapter;

    //No Items
    String[] noItems = {"You do not have any history found, checkout some carts now!"};
    String[] errorItems = {"Unable to get the history folder. Is your SD Card plugged in?"};
    StringRecyclerAdapter noItemsadapter, errorItemsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_history, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        //Init
        layout = (CoordinatorLayout) getActivity().findViewById(R.id.activity_coordinator_layout);
        alwaysHideThisView = (CardView) getActivity().findViewById(R.id.card_view);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.activity_fab);
        recyclerView = (RecyclerView) v.findViewById(R.id.rv_history);

        //RecyclerView Init
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        checkAndUpdateAdapter();

        //Default init
        noItemsadapter = new StringRecyclerAdapter(noItems);
        errorItemsAdapter = new StringRecyclerAdapter(errorItems);

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_fragment_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_delete_all) {
            new AlertDialog.Builder(getActivity()).setTitle("Delete all history")
                    .setMessage("Are you sure you wish to clear all of your history?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (HistoryObjectHelper.deleteAllHistoryFiles(getContext())){
                                if (layout != null)
                                    Snackbar.make(layout, "All history deleted", Snackbar.LENGTH_SHORT).show();
                                else
                                    ToastHelper.createShortToast(getContext(), "All history deleted");
                                onRefresh();
                            }
                        }
                    }).setNegativeButton(android.R.string.no, null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkAndUpdateAdapter(){
        List<HistoryItem> items = HistoryObjectHelper.getAllHistoryItemFromFile(getActivity().getApplicationContext());
        if (items == null){
            Log.e(StaticReferences.TAG, "Unable to get history folder");
            recyclerView.setAdapter(errorItemsAdapter);
            return;
        }

        if (items.size() == 0){
            Log.i(StaticReferences.TAG, "No history found");
            recyclerView.setAdapter(noItemsadapter);
        } else {
            adapter = new HistoryListRecyclerAdapter(items, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onRefresh() {
        checkAndUpdateAdapter();
    }

    @Override
    public void onSwipeRefresh(){
        Log.d(StaticReferences.TAG, "History Fragment triggered");
        if (alwaysHideThisView != null)
            alwaysHideThisView.setVisibility(View.GONE);
        if (fab != null){
            fab.hide();
        }

        onRefresh();
    }

    @Override
    public void cartItemAdded(CartItem item) {

    }

    @Override
    public void deleteCategory(ListCategory category) {

    }

    @Override
    public void updateCategory(ListCategory newCategory) {

    }

    @Override
    public void deleteItem(ListItem item) {

    }

    @Override
    public void updateItem(ListItem item) {

    }

    @Override
    public void cartItemClicked(CartItem item) {

    }

    @Override
    public void selectHistoryItem(HistoryItem item) {
        String backToJson = HistoryObjectHelper.generateHistoryJsonString(item);
        Intent intent = new Intent(getActivity(), ViewHistoryItemActivity.class);
        intent.putExtra("itemJSON", backToJson);
        startActivity(intent);
    }

    @Override
    public void deleteHistoryFile(final HistoryItem item) {
        new AlertDialog.Builder(getActivity()).setTitle("Delete this history?")
                .setMessage("Are you sure you wish to remove this history?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (HistoryObjectHelper.deleteHistoryFile(getContext(), item)){
                            Snackbar.make(getActivity().findViewById(R.id.activity_coordinator_layout), "History Deleted", Snackbar.LENGTH_SHORT)
                                    .show();
                        } else {
                            Snackbar.make(getActivity().findViewById(R.id.activity_coordinator_layout), "Unable to delete history", Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                        onRefresh();
                    }
                }).show();
    }
}
