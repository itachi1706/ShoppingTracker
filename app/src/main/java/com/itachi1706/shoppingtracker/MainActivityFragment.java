package com.itachi1706.shoppingtracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.ListBase;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.Objects.ListItem;
import com.itachi1706.shoppingtracker.utility.CartJsonHelper;
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
        boolean isDebug = sp.getBoolean("debug", false);
        if (!isDebug)
            checkAndUpdateAdapter();
        else
            checkAndUpdateAdapterDebug();
    }

    @Override
    public void cartItemAdded(final CartItem cartItem) {
        CoordinatorLayout layout = (CoordinatorLayout) getActivity().findViewById(R.id.activity_coordinator_layout);
        Snackbar.make(layout, "Added to Cart", Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CartJsonHelper.removeCartItemFromJsonCart(cartItem, sp);
                    }
                }).show();
    }

    private void checkAndUpdateAdapter() {
        ListDB db = new ListDB(getActivity());
        if (db.isEmptyItems()) {
            //Null
            recyclerView.setAdapter(noItemsadapter);
        } else {
            //All Items
            ArrayList<ListCategory> categories = db.getAllCategories();
            //Uncatergorized items
            ArrayList<ListItem> items = db.getAllUncategorizedItems();

            for (ListCategory category : categories) {
                category.setChildProducts(db.getAllItemsByCategory(category));
            }

            List<ListBase> baseItems = mergeCatAndItems(categories, items);
            adapter = new ItemListRecyclerAdapter(baseItems, sp, this);
            recyclerView.setAdapter(adapter);
        }
    }

    private List<ListBase> mergeCatAndItems(ArrayList<ListCategory> categories, ArrayList<ListItem> items){
        List<ListBase> result = new ArrayList<>();
        for (ListCategory category : categories){
            result.add(category);
        }
        for (ListItem item : items){
            result.add(item);
        }

        return result;
    }


    //Sample Class for testing
    private void checkAndUpdateAdapterDebug(){
        List<ListBase> items = GenerateSampleData.generateItems();
        adapter = new ItemListRecyclerAdapter(items, sp, this);
        recyclerView.setAdapter(adapter);
    }
}
