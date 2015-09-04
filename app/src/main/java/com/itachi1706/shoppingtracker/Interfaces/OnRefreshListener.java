package com.itachi1706.shoppingtracker.Interfaces;

import com.itachi1706.shoppingtracker.Objects.CartItem;

/**
 * Created by Kenneth on 9/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker
 */
public interface OnRefreshListener {
    void onRefresh();
    void cartItemAdded(CartItem item);
}
