package com.itachi1706.shoppingtracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itachi1706.shoppingtracker.Adapters.ItemListRecyclerAdapter;
import com.itachi1706.shoppingtracker.Adapters.StringRecyclerAdapter;
import com.itachi1706.shoppingtracker.Database.ListDB;
import com.itachi1706.shoppingtracker.Interfaces.OnRefreshListener;
import com.itachi1706.shoppingtracker.Objects.ListBase;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.utility.GenerateSampleData;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnRefreshListener {

    public MainActivityFragment() {
    }

    View v;
    SharedPreferences sp;

    RecyclerView recyclerView;
    CardView alwaysHideThisView;    //TODO: Make this card view look nicer lol. Black is ugly :P

    //No Items
    String[] noItems = {"No Items found. Add some items now!"};
    StringRecyclerAdapter noItemsadapter;

    //Has Items Adapter
    ItemListRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main_screen, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        //No Items
        noItemsadapter = new StringRecyclerAdapter(noItems);

        alwaysHideThisView = (CardView) getActivity().findViewById(R.id.card_view);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_items);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        onRefresh();

        return v;
    }

    @Override
    public void onRefresh() {
        Log.d(StaticReferences.TAG, "Main Fragment triggered");
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.activity_fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.plus_black_48));
        alwaysHideThisView.setVisibility(View.GONE);
        checkAndUpdateAdapter();
    }

    //TODO: Rename back to checkAndUpdateAdapter() after test
    private void checkAndUpdateAdapterReal() {
        ListDB db = new ListDB(getActivity());
        if (db.isEmptyItems()) {
            //Null
            recyclerView.setAdapter(noItemsadapter);
        } else {
            //All Items
            ArrayList<ListCategory> categories = db.getAllCategories();
            for (ListCategory category : categories) {
                category.setChildProducts(db.getAllItemsByCategory(category));
            }
        }
    }


    //Sample Class for testing
    //TODO: Remove this class after test
    private void checkAndUpdateAdapter(){
        List<ListBase> items = GenerateSampleData.generateItems();
        adapter = new ItemListRecyclerAdapter(items, sp);
        recyclerView.setAdapter(adapter);
    }
}
