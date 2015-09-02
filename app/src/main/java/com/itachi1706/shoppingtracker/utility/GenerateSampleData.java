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

        list.add(new ListCategory("Potato"));
        list.add(new ListItem(0, "Potato Salad"));
        list.add(new ListItem(0, "Potato Salad with Barcode", "91111111"));
        return list;
    }
}
