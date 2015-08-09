package com.itachi1706.shoppingtracker;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itachi1706.shoppingtracker.Adapters.StringRecyclerAdapter;
import com.itachi1706.shoppingtracker.Interfaces.OnRefreshListener;

public class CartFragment extends Fragment implements OnRefreshListener {

    public CartFragment() {
    }

    View v;

    RecyclerView recyclerView;

    //No Items
    String[] noItems = {"No Items in Cart found. Add some items to cart now!"};
    StringRecyclerAdapter noItemsadapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_cart, container, false);

        //No Items
        noItemsadapter = new StringRecyclerAdapter(noItems);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_carts);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        checkAndUpdateAdapter();

        return v;
    }

    @Override
    public void onRefresh() {
        checkAndUpdateAdapter();
    }

    private void checkAndUpdateAdapter()
    {
        //TODO Implement Cart JSON and stuff
        //TODO Setting standard null initially
        recyclerView.setAdapter(noItemsadapter);
    }
}
