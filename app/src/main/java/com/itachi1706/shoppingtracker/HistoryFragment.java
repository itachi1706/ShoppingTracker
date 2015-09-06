package com.itachi1706.shoppingtracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
    //TODO: Create a history adapter for the recycler view

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
            if (layout != null)
                Snackbar.make(layout, "TODO: DELETE ALL", Snackbar.LENGTH_SHORT).show();
            else
                ToastHelper.createShortToast(getContext(), "TODO: DELETE ALL");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkAndUpdateAdapter(){
        //TODO: Get all history
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
            //TODO: Place all the history into the adapter
            recyclerView.setAdapter(noItemsadapter);
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
}
