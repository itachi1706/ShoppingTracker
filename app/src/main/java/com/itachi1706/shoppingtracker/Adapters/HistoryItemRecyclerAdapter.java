package com.itachi1706.shoppingtracker.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.R;
import com.itachi1706.shoppingtracker.utility.StaticMethods;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker.Adapters
 */
public class HistoryItemRecyclerAdapter extends RecyclerView.Adapter<HistoryItemRecyclerAdapter.CartViewHolder> {

    private List<CartItem> cartList;

    public HistoryItemRecyclerAdapter(List<CartItem> cartItems)
    {
        this.cartList = cartItems;
    }

    public HistoryItemRecyclerAdapter(){this.cartList = new ArrayList<>();}

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
        cartViewHolder.basePrice.setText(StaticMethods.getPriceSymbol() + df.format(s.getBasePrice()));
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


    public class CartViewHolder extends RecyclerView.ViewHolder {

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
        }
    }
}
