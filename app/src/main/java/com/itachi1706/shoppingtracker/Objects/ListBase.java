package com.itachi1706.shoppingtracker.Objects;

/**
 * Created by Kenneth on 9/2/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.Objects
 */
public class ListBase {

    private boolean isChild = false;

    public ListBase(){}

    public ListBase(boolean isChild) {
        this.isChild = isChild;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setIsChild(boolean isChild) {
        this.isChild = isChild;
    }
}
