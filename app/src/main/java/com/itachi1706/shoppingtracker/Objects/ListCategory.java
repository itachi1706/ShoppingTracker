package com.itachi1706.shoppingtracker.Objects;

/**
 * Created by Kenneth on 1/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker.Objects
 */
public class ListCategory {

    private int id;
    private String name;

    public ListCategory() {
    }

    public ListCategory(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ListCategory(String name)
    {
        this.name = name;

    }
}
