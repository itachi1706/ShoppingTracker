package com.itachi1706.shoppingtracker.Objects;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kenneth on 9/6/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.Objects
 */
public class HistoryItem {

    private CartItem[] cart;
    private long date;
    private double total;

    public HistoryItem(CartItem[] cart, double total){
        this.date = System.currentTimeMillis();
        this.cart = cart;
        this.total = total;
    }

    public HistoryItem(List<CartItem> cart, double total){
        this.date = System.currentTimeMillis();
        this.cart = cart.toArray(new CartItem[cart.size()]);
        this.total = total;
    }

    public HistoryItem(CartItem[] cart, double total, long date){
        this.cart = cart;
        this.date = date;
        this.total = total;
    }

    public CartItem[] getCart() {
        return cart;
    }

    public void setCart(CartItem[] cart) {
        this.cart = cart;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
