package com.itachi1706.shoppingtracker.Objects;

import java.util.List;

/**
 * Created by Kenneth on 1/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker.Objects
 */
public class ListCategory extends ListBase {

    private long id;
    private String name;

    private List<ListItem> childProducts;

    public ListCategory() {
        super();
    }

    public ListCategory(long id, String name)
    {
        super();
        this.id = id;
        this.name = name;
    }

    public ListCategory(String name)
    {
        super();
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ListItem> getChildProducts() {
        return childProducts;
    }

    public void setChildProducts(List<ListItem> childProducts) {
        this.childProducts = childProducts;
    }

    public boolean hasChild() {
        return this.childProducts != null && this.childProducts.size() != 0;

    }
}
