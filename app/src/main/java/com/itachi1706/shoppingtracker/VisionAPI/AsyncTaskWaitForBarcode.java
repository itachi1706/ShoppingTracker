package com.itachi1706.shoppingtracker.VisionAPI;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.itachi1706.shoppingtracker.utility.StaticReferences;

/**
 * Created by Kenneth on 9/3/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.VisionAPI
 */
public class AsyncTaskWaitForBarcode extends AsyncTask<Void, Void, Void> {

    private Activity activity;

    public AsyncTaskWaitForBarcode(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (true) {
            if (StaticReferences.barcode != null) {
                break;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Toast.makeText(activity, "Found barcode", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}
