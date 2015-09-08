package com.itachi1706.shoppingtracker.utility;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
        return sp.getString("currency_symbol","$");
    }
}
