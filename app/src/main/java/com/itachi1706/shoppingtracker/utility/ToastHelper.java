package com.itachi1706.shoppingtracker.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Kenneth on 9/1/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.utility
 */
public class ToastHelper {

    public static void createShortToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
