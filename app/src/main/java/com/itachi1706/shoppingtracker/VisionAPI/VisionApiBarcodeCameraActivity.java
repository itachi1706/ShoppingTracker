package com.itachi1706.shoppingtracker.VisionAPI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.itachi1706.shoppingtracker.R;

import java.io.IOException;

public class VisionApiBarcodeCameraActivity extends AppCompatActivity {

    CameraSource mCameraSource;
    CameraSourcePreview mPreview;
    GraphicOverlay mGraphicOverlay;

    BarcodeDetector barcodeDetector;
    BarcodeTrackerFactory barcodeFactory;

    AsyncTask asyncTask;

    SharedPreferences sp;

    private static final String TAG = "VisionAPI_Barcode";

    private static final int RC_HANDLE_CAMERA_PERM = 2; //Perm Request Codes needs to be < 256


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_camera);

        mPreview = (CameraSourcePreview) findViewById(R.id.barcode_preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.barcode_overlay);

        sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        //Android M requires permission requesting, so executing that :P
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED){
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource(); //start
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop(); //stop
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null)
            mCameraSource.release(); //release the resources
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if (asyncTask != null){
            asyncTask.cancel(true);
            asyncTask = null;
        }
    }

    //Camera Source Creation
    private void createCameraSource(){
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        barcodeDetector = new BarcodeDetector.Builder(context).build();
        barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        mCameraSource = new CameraSource.Builder(context, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .build();
    }

    private void startCameraSource(){
        try {
            mPreview.start(mCameraSource, mGraphicOverlay);
            boolean startAsyncTask = sp.getBoolean("vision_continuous_test", false);
            if (!startAsyncTask)
                asyncTask = new AsyncTaskWaitForBarcode(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (IOException e){
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    //Perm Handling
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission(){
        Log.w(TAG, "Camera permission not granted, requesting...");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Log.d(TAG, "We do not need to show permission rationale");
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        Log.i(TAG, "Showing Request Permissions Rationale");
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, listener)
                .show();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        Log.e(TAG, "Permission was not granted dialog prompt");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Not Granted")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }



}
