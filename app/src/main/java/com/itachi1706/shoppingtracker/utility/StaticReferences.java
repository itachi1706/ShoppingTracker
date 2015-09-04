package com.itachi1706.shoppingtracker.utility;


import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Kenneth on 9/2/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.AsyncTasks
 */
public class StaticReferences {

    public static final int HTTP_TIMEOUT = 15000; //15 seconds
    public static final String TAG = "ShoppingTracker";
    public static Context APP_CONTEXT;

    public static Barcode barcode = null;
}
