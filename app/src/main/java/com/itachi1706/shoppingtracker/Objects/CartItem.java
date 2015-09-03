package com.itachi1706.shoppingtracker.Objects;

/**
 * Created by Kenneth on 9/3/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.Objects
 */
public class CartItem {

    private int id;
    private int qty;
    private double basePrice;
    private ListItem item;

    public CartItem(){}

    public CartItem(int id, int qty, double basePrice) {
        this.id = id;
        this.qty = qty;
        this.basePrice = basePrice;
    }

    public CartItem(int id, int qty, double basePrice, ListItem item) {
        this.id = id;
        this.qty = qty;
        this.basePrice = basePrice;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public ListItem getItem() {
        return item;
    }

    public void setItem(ListItem item) {
        this.item = item;
    }
}
