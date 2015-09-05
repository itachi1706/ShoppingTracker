package com.itachi1706.shoppingtracker.Interfaces;

import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.Objects.ListItem;

/**
 * Created by Kenneth on 9/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker
 */
public interface OnRefreshListener {
    void onRefresh();
    void cartItemAdded(CartItem item);
    void deleteCategory(ListCategory category);
    void updateCategory(ListCategory newCategory);
    void deleteItem(ListItem item);
    void updateItem(ListItem item);
}
