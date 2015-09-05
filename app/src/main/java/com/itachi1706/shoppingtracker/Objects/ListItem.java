package com.itachi1706.shoppingtracker.Objects;

/**
 * Created by Kenneth on 1/8/2015
 * for Shopping Tracker in package com.itachi1706.shoppingtracker.Objects
 */
public class ListItem extends ListBase {

    private long id;
    private String name;
    private String barcode;
    private long category;
    //private Drawable image;   //Soon(tm)

    public ListItem() {
        super(true);
    }

    public ListItem(String name){
        super(true);
        this.name = name;
    }

    public ListItem(String name, String barcode){
        super(true);
        this.name = name;
        this.barcode = barcode;
    }

    public ListItem(long id, String name, String barcode, long category) {
        super(true);
        this.id = id;
        this.name = name;
        this.barcode = barcode;
        this.category = category;
    }

    public ListItem(long id, String name, long category) {
        super(true);
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public ListItem(String name, String barcode, long category) {
        super(true);
        this.name = name;
        this.barcode = barcode;
        this.category = category;
    }

    public ListItem(String name, long category) {
        super(true);
        this.name = name;
        this.category = category;
    }

    public ListItem(long id, String name, String barcode) {
        super(true);
        this.id = id;
        this.name = name;
        this.barcode = barcode;
    }

    public ListItem(long id, String name) {
        super(true);
        this.id = id;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }
}
