package com.itachi1706.shoppingtracker.Objects;

/**
 * Created by Kenneth on 9/3/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.Objects
 */
public class JSONCart {

    private long id;
    private int qty;
    private double basePrice;

    public JSONCart(){}

    public JSONCart(long id, int qty, double basePrice) {
        this.id = id;
        this.qty = qty;
        this.basePrice = basePrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
}
