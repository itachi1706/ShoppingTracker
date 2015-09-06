package com.itachi1706.shoppingtracker.utility;


import android.content.Context;

import com.google.android.gms.vision.barcode.Barcode;
import com.itachi1706.shoppingtracker.Objects.ListItem;

/**
 * Created by Kenneth on 9/2/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.AsyncTasks
 */
public class StaticReferences {

    public static final int HTTP_TIMEOUT = 15000; //15 seconds
    public static final String TAG = "ShoppingTracker";
    public static Context APP_CONTEXT;
    public static boolean isMainSwiped = false;
    public static boolean isFirstLaunched = true;

    public static Barcode barcode = null;
    public static ListItem updateItemTmp = null;
}
