package com.itachi1706.shoppingtracker.Adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.shoppingtracker.CartFragment;
import com.itachi1706.shoppingtracker.MainActivityFragment;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.ListBase;
import com.itachi1706.shoppingtracker.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Kenneth on 9/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker.Adapters
 */
public class CartItemRecyclerAdapter extends RecyclerView.Adapter<CartItemRecyclerAdapter.CartViewHolder> {

    private List<CartItem> cartList;
    private Fragment fragment;

    public CartItemRecyclerAdapter(List<CartItem> cartItems, Fragment fragment)
    {
        this.cartList = cartItems;
        this.fragment = fragment;
    }

    @Override
    public int getItemCount()
    {
        return cartList.size();
    }

    @Override
    public void onBindViewHolder(CartViewHolder cartViewHolder, int i)
    {
        CartItem s  = cartList.get(i);
        cartViewHolder.title.setText(s.getItem().getName());
        if (s.getItem().getBarcode() != null){
            cartViewHolder.barcode.setText(s.getItem().getBarcode());
        } else {
            cartViewHolder.barcode.setText("");
        }

        cartViewHolder.quantity.setText("Qty: " + s.getQty());

        DecimalFormat df = new DecimalFormat("0.00");
        cartViewHolder.basePrice.setText("$" + df.format(s.getBasePrice()));
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recyclerview_cart_items, viewGroup, false);

        return new CartViewHolder(itemView);
    }

    public void addItem(CartItem item){
        int pos = cartList.size();
        cartList.add(item);
        notifyItemInserted(pos);
    }

    public List<CartItem> getCartItems(){
        return cartList;
    }

    public void removeItem(CartItem item){
        int pos = -1;
        for (int i = 0; i < cartList.size(); i++){
            if (cartList.get(i) == item){
                pos = i;
                break;
            }
        }

        if (pos == -1) return;

        cartList.remove(pos);
        notifyItemRemoved(pos);
    }


    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title;
        protected TextView barcode;
        protected TextView quantity;
        protected TextView basePrice;

        public CartViewHolder(View v)
        {
            super(v);
            title = (TextView) v.findViewById(R.id.cart_title);
            barcode = (TextView) v.findViewById(R.id.cart_barcode);
            quantity = (TextView) v.findViewById(R.id.cart_qty);
            basePrice = (TextView) v.findViewById(R.id.cart_price);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //TODO: Implement edit and remove for cart items
            Toast.makeText(v.getContext(), title.getText(), Toast.LENGTH_SHORT).show();
            int position = this.getLayoutPosition();
            CartItem item = cartList.get(position);

            if (fragment instanceof MainActivityFragment) return;

            CartFragment f = (CartFragment) fragment;
            f.cartItemClicked(item);
        }

    }
}
