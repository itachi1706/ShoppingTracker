package com.itachi1706.shoppingtracker.utility;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.HistoryItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/6/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.utility
 */
public class HistoryObjectHelper {

    private static String generateHistoryJsonString(HistoryItem historyItems){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(historyItems, HistoryItem.class);
    }

    private static HistoryItem getHistoryItemObjectFromJson(String jsonString){
        Gson gson = new Gson();
        return gson.fromJson(jsonString, HistoryItem.class);
    }

    private static String getFolder(Context context){
        return context.getExternalFilesDir(null) + File.separator + "history";
    }

    private static boolean createFolderIfNotExists(Context context){
        if (context.getExternalFilesDir(null) == null)
            return false;

        String folderPath = getFolder(context);
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                Log.i(StaticReferences.TAG, "History Folder Created");
                return true;
            }
            return false;
        }

        if (!folder.isDirectory()){
            if (folder.delete() && folder.mkdir()){
                Log.i(StaticReferences.TAG, "History Folder Recreated");
                return true;
            }
            return false;
        }

        return true;
    }

    public static boolean createNewHistoryFile(Context context, HistoryItem item){
        if (!createFolderIfNotExists(context)){
            Log.e(StaticReferences.TAG, "Unable to create folder to store history, Aborting...");
            return false;
        }
        String timeStamp = item.getDate() + "";
        String fileName = getFolder(context) + File.separator + "history-" + timeStamp + ".json";
        String historyJson = generateHistoryJsonString(item);
        File file = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(historyJson);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(StaticReferences.TAG, "Unable to write to file. Aborting...");
            Crashlytics.logException(e);
            return false;
        }
        Log.i(StaticReferences.TAG, "Wrote " + fileName + " to file!");
        return true;
    }

    public static List<HistoryItem> getAllHistoryItemFromFile(Context context){
        if (!createFolderIfNotExists(context)){
            Log.e(StaticReferences.TAG, "Unable to create folder to store history, Aborting...");
            return null;
        }

        String folderPath = getFolder(context);
        File folder = new File(folderPath);

        File[] fileList = folder.listFiles();

        List<HistoryItem> itemList = new ArrayList<>();

        for (File file : fileList){
            String ext = getExtension(file);
            if (!ext.equalsIgnoreCase("json")) continue;

            StringBuilder line;
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String currentLine;
                line = new StringBuilder();

                while ((currentLine = reader.readLine()) != null){
                    line.append(currentLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(StaticReferences.TAG, "Unable to read from file. Aborting this file...");
                Crashlytics.logException(e);
                continue;
            }

            String jsonString = line.toString();
            HistoryItem item = getHistoryItemObjectFromJson(jsonString);
            itemList.add(item);
        }
        return itemList;
    }

    public static int getHistoryFileListSize(Context context){
        List<HistoryItem> list = getAllHistoryItemFromFile(context);
        if (list == null)
            return 0;
        return list.size();
    }

    private static String getExtension(File file){
        if (file == null) return null;

        String name = file.getName();
        int extIndex = name.lastIndexOf(".");
        if (extIndex == -1) return "";
        else return name.substring(extIndex + 1);
    }

    public static boolean createNewHistoryFile(Context context, List<CartItem> item, double total){
        HistoryItem historyItem = new HistoryItem(item, total);
        return createNewHistoryFile(context, historyItem);
    }

    public static boolean createNewHistoryFile(Context context, CartItem[] item, double total){
        HistoryItem historyItem = new HistoryItem(item, total);
        return createNewHistoryFile(context, historyItem);
    }

}
