package com.itachi1706.shoppingtracker.Objects;

/**
 * Created by Kenneth on 1/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker.Objects
 */
public class ListItem extends ListBase {

    private int id;
    private String name;
    private String barcode;
    private int category = -1;
    //private Drawable image;   //Soon(tm)

    public ListItem() {
        super(true);
    }

    public ListItem(int id, String name, String barcode, int category) {
        super(true);
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.category = category;
    }

    public ListItem(int id, String name, int category) {
        super(true);
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public ListItem(String name, String barcode, int category) {
        super(true);
        this.name = name;
        this.barcode = barcode;
        this.category = category;
    }

    public ListItem(String name, int category) {
        super(true);
        this.name = name;
        this.category = category;
    }

    public ListItem(int id, String name, String barcode) {
        super(true);
        this.id = id;
        this.name = name;
        this.barcode = barcode;
    }

    public ListItem(int id, String name) {
        super(true);
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
