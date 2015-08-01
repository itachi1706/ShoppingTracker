package com.itachi1706.shoppingtracker.Objects;

/**
 * Created by Kenneth on 1/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker.Objects
 */
public class ListItem {

    private int id;
    private String name;
    private String barcode;
    private ListCategory category;

    public ListItem() {
    }

    public ListItem(int id, String name, String barcode, ListCategory category) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.category = category;
    }

    public ListItem(int id, String name, ListCategory category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public ListItem(String name, String barcode, ListCategory category) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
    }

    public ListItem(String name, ListCategory category) {
        this.name = name;
        this.category = category;
    }

    public ListItem(int id, String name, String barcode) {
        this.id = id;
        this.name = name;
        this.barcode = barcode;
    }

    public ListItem(int id, String name) {
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public ListCategory getCategory() {
        return category;
    }

    public void setCategory(ListCategory category) {
        this.category = category;
    }
}
