package com.itachi1706.shoppingtracker.VisionAPI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.widget.Toast;

import com.itachi1706.shoppingtracker.utility.StaticReferences;

/**
 * Created by Kenneth on 9/3/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.VisionAPI
 */
public class AsyncTaskWaitForBarcode extends AsyncTask<Void, Void, Void> {

    private Activity activity;
    private boolean isInterrupted = false;

    public AsyncTaskWaitForBarcode(Activity activity){
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        StaticReferences.barcode = null;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            isInterrupted = true;
            return null;
        }
        while (true) {
            if (isCancelled()) {
                isInterrupted = true;
                return null;
            }
            if (StaticReferences.barcode != null) {
                break;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isInterrupted) return;
        Vibrator v = (Vibrator) activity.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        Toast.makeText(activity, "Found barcode", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}
