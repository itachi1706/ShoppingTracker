package com.itachi1706.shoppingtracker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.shoppingtracker.Adapters.CartItemRecyclerAdapter;
import com.itachi1706.shoppingtracker.Adapters.StringRecyclerAdapter;
import com.itachi1706.shoppingtracker.Interfaces.OnRefreshListener;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.JSONCart;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.Objects.ListItem;
import com.itachi1706.shoppingtracker.utility.CartJsonHelper;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.text.DecimalFormat;
import java.util.List;

public class CartFragment extends Fragment implements OnRefreshListener {

    public CartFragment() {
    }

    View v;

    RecyclerView recyclerView;

    //From Activity
    TextView totalPrice;
    CardView showThisView;
    FloatingActionButton fab;

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
        fab = (FloatingActionButton) getActivity().findViewById(R.id.activity_fab);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        checkAndUpdateAdapter();

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_fragment_cart, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_checkout) {
            //TODO: Checkout and save cart, for now its cleared
            DecimalFormat df = new DecimalFormat("0.00");
            CartJsonHelper.clearCart(sp);
            new AlertDialog.Builder(getActivity()).setTitle("Checkout")
                    .setMessage("Checkout succeeded. Your cart total was $" + df.format(getTotalSum()))
                    .setPositiveButton(android.R.string.ok, null).show();
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        Log.d(StaticReferences.TAG, "Cart Fragment triggered");
        if (fab != null)
            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.barcode_48));
        if (totalPrice != null) {
            totalPrice.setVisibility(View.VISIBLE);
            totalPrice.setText("Total: $0.00");
        }
        if (showThisView != null)
            showThisView.setVisibility(View.VISIBLE);

        checkAndUpdateAdapter();
    }

    @Override
    public void cartItemAdded(CartItem cartItem) {}

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
        processQuantityAndBarcodePrompt(item);
    }

    private void processQuantityAndBarcodePrompt(final CartItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(item.getItem().getName());
        LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_add_item_to_cart, null);

        final EditText qty = (EditText) view.findViewById(R.id.dialog_quantity);
        final EditText price = (EditText) view.findViewById(R.id.dialog_price);
        qty.setText(item.getQty() + "");
        price.setText(item.getBasePrice() + "");
        builder.setView(view);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantity = qty.getText().toString();
                String itemPrice = price.getText().toString();
                quantity = quantity.trim();
                itemPrice = itemPrice.trim();
                if (quantity.equals("") || itemPrice.equals("")) {
                    Toast.makeText(getActivity(), "You need to enter a price and quantity", Toast.LENGTH_SHORT).show();
                    return;
                }
                final CartItem cartItem = new CartItem(item.getItem().getId(), Integer.parseInt(qty.getText().toString()), Double.parseDouble(price.getText().toString()), item.getItem());
                CartJsonHelper.addCartItemToJsonCart(cartItem, sp);
                onRefresh();
                Toast.makeText(getActivity(), "Cart Item Updated", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CartJsonHelper.removeCartItemFromJsonCart(item, sp);
                adapter.removeItem(item);
                calculateTotal();
                Toast.makeText(getActivity(), "Item Removed from Cart", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

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
            adapter = new CartItemRecyclerAdapter(cartItems, this);
            recyclerView.setAdapter(adapter);

            //Calculate and Display Total
            calculateTotal();
        }
    }

    private void calculateTotal(){
        double total = getTotalSum();

        DecimalFormat df = new DecimalFormat("0.00");
        totalPrice.setText("Total: $" + df.format(total));
    }

    private double getTotalSum(){
        List<CartItem> cartItems = adapter.getCartItems();
        double total = 0;
        for (CartItem item : cartItems){
            total += (item.getQty() * item.getBasePrice());
        }
        return total;
    }
}
