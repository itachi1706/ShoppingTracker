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
import android.widget.TextView;

import com.itachi1706.shoppingtracker.Adapters.CartItemRecyclerAdapter;
import com.itachi1706.shoppingtracker.Adapters.StringRecyclerAdapter;
import com.itachi1706.shoppingtracker.Interfaces.OnRefreshListener;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.JSONCart;
import com.itachi1706.shoppingtracker.utility.CartJsonHelper;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.text.DecimalFormat;
import java.util.List;

public class CartFragment extends Fragment implements OnRefreshListener {

    public CartFragment() {
    }

    View v;

    RecyclerView recyclerView;
    TextView totalPrice;
    CardView showThisView;

    //Has Items
    CartItemRecyclerAdapter adapter;

    //No Items
    String[] noItems = {"No Items in Cart found. Add some items to cart now!"};
    StringRecyclerAdapter noItemsadapter;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_cart, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        //No Items
        noItemsadapter = new StringRecyclerAdapter(noItems);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_carts);
        totalPrice = (TextView) getActivity().findViewById(R.id.activity_total);
        showThisView = (CardView) getActivity().findViewById(R.id.card_view);

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
        Log.d(StaticReferences.TAG, "Cart Fragment triggered");
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.activity_fab);
        fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.barcode_48));
        totalPrice.setVisibility(View.VISIBLE);
        totalPrice.setText("Total: $0.00");
        showThisView.setVisibility(View.VISIBLE);
        checkAndUpdateAdapter();
    }

    @Override
    public void cartItemAdded(CartItem cartItem) {}

    private void checkAndUpdateAdapter()
    {
        JSONCart[] items = CartJsonHelper.getJsonCart(sp);
        if (items.length == 0){
            Log.i(StaticReferences.TAG, "No existing cart found");
            recyclerView.setAdapter(noItemsadapter);
        } else {
            //Has cart, convert and show it
            Log.i(StaticReferences.TAG, "Found an existing cart, displaying it...");
            List<CartItem> cartItems = CartJsonHelper.convertJsonCartToCartItems(items);
            adapter = new CartItemRecyclerAdapter(cartItems);
            recyclerView.setAdapter(adapter);

            //Calculate and Display Total
            calculateTotal();
        }
    }

    private void calculateTotal(){
        List<CartItem> cartItems = adapter.getCartItems();
        double total = 0;
        for (CartItem item : cartItems){
            total += (item.getQty() * item.getBasePrice());
        }

        DecimalFormat df = new DecimalFormat("0.00");
        totalPrice.setText("Total: $" + df.format(total));
    }
}
