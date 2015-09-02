package com.itachi1706.shoppingtracker.utility;

import com.itachi1706.shoppingtracker.Objects.ListBase;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.Objects.ListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/2/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.utility
 */
public class GenerateSampleData {

    public static List<ListBase> generateItems(){
        List<ListBase> list = new ArrayList<>();

        //Stuff
        list.add(new ListCategory("Potato"));
        list.add(new ListItem(0, "Potato Salad"));
        list.add(new ListItem(1, "Potato Salad with Barcode", "91111111"));

        //Lets place something to expand
        List<ListItem> childItems = new ArrayList<>();
        childItems.add(new ListItem(2, "Banana", 1));
        childItems.add(new ListItem(3, "Banana with Salad", "911", 1));
        ListCategory cat = new ListCategory(1, "Banana Category");
        cat.setChildProducts(childItems);
        list.add(cat);


        //Lets place another something to expand
        List<ListItem> childItems2 = new ArrayList<>();
        childItems2.add(new ListItem(5, "Grapes", 2));
        childItems2.add(new ListItem(6, "Grapes with French Fry", "111", 2));
        ListCategory cat2 = new ListCategory(2, "Grapes Category");
        cat2.setChildProducts(childItems2);
        list.add(cat2);

        //Lets make it scroll a little
        list.add(new ListItem(4, "Potato Salad with 1", "91111113"));
        list.add(new ListItem(7, "Potato Salad with 2", "91111114"));
        list.add(new ListItem(8, "Potato Salad with 3", "91111115"));
        list.add(new ListItem(9, "Potato Salad with 4", "91111116"));
        list.add(new ListItem(10, "Potato Salad with 5", "91111117"));
        return list;
    }
}
