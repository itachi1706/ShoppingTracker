package com.itachi1706.shoppingtracker.utility;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kenneth on 9/2/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.utility
 */
public class StaticMethods {

    public static String getChangelogFromArrayList(ArrayList<String> changelogArray){
        /* Legend of Stuff
         * 1st Line - Current Version Code check
         * 2nd Line - Current Version Number
         * 3rd Line - Link to New Version
         * # - Changelog Version Number (Bold this)
         * * - Points
         * @ - Break Line
         */
        StringBuilder changelogStringBuilder = new StringBuilder();
        changelogStringBuilder.append("Latest Version: ").append(changelogArray.get(1))
                .append("-b").append(changelogArray.get(0)).append("<br /><br />");

        for (String changelog : changelogArray){
            if (changelog.startsWith("#"))
                changelogStringBuilder.append("<b>").append(changelog.replace('#', ' ')).append("</b><br />");
            else if (changelog.startsWith("*"))
                changelogStringBuilder.append(" - ").append(changelog.replace('*', ' ')).append("<br />");
            else if (changelog.startsWith("@"))
                changelogStringBuilder.append("<br />");
        }

        return changelogStringBuilder.toString();
    }

    public static String getChangelogFromArray(String[] changelog){
        ArrayList<String> changelogArrList = new ArrayList<>();
        Collections.addAll(changelogArrList, changelog);
        return getChangelogFromArrayList(changelogArrList);
    }

    public static String getPriceSymbol(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(StaticReferences.APP_CONTEXT);
        return sp.getString("currency_symbol", "$");
    }

    public static String getFilePath(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(StaticReferences.APP_CONTEXT);
        int storageLocation = Integer.parseInt(sp.getString("storageLocation", "2"));
        if (storageLocation == 2){
            //Internal SD
            return context.getExternalFilesDir(null) + File.separator;
        } else if (storageLocation == 3) {
            //External SD

            //If pre-Kitkat, return internal sd instead
            if (Build.VERSION.SDK_INT >= 19) {
                File[] externalLocation = context.getExternalFilesDirs(null);
                if (externalLocation.length > 1){
                    return externalLocation[1] + File.separator;
                }
                return externalLocation[0] + File.separator;
            }
            return context.getExternalFilesDir(null) + File.separator;
        }

        //Internal
        return context.getFilesDir() + File.separator;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String debugStorageLocation(Context context){
        File[] externalLocation = context.getExternalFilesDirs(null);
        StringBuilder builder = new StringBuilder();
        Log.d("DEBUG-STORAGELOCATION", context.getFilesDir().getAbsolutePath());
        builder.append("Internal: ").append(context.getFilesDir().getAbsolutePath()).append("<br />");
        builder.append("External: ").append("<br />");
        for (File file : externalLocation){
            Log.d("DEBUG-STORAGELOCATION", file.getAbsolutePath());
            builder.append(file.getAbsolutePath()).append("<br />");
        }
        return builder.toString();
    }
}
