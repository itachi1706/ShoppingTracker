package com.itachi1706.shoppingtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.barcode.Barcode;
import com.itachi1706.shoppingtracker.Database.ListDB;
import com.itachi1706.shoppingtracker.FallbackBarcodeScanner.IntentIntegrator;
import com.itachi1706.shoppingtracker.FallbackBarcodeScanner.IntentResult;
import com.itachi1706.shoppingtracker.Objects.LegacyBarcode;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.VisionAPI.VisionApiBarcodeCameraActivity;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.util.ArrayList;
import java.util.List;

public class AddItemToDB extends AppCompatActivity {

    private static int VISION_REQUEST_CODE = 100;
    private static boolean isLegacyBarcode = false;

    private boolean newCategoryCreated = false;
    private ListCategory categorySelected = null;
    private List<ListCategory> categoryList;

    //App tools
    EditText name, barcode, category;
    Spinner categorySelection;
    Button scanBarcodeBtn;
    FloatingActionButton addItem;
    CoordinatorLayout coordinatorLayout;
    TextInputLayout nameTil, barcodeTil, categoryTil;

    //Spinner
    List<String> spinnerList = new ArrayList<>();
    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_db);

        //Declare Views
        name = (EditText) findViewById(R.id.activity_add_name);
        barcode = (EditText) findViewById(R.id.activity_add_barcode);
        scanBarcodeBtn = (Button) findViewById(R.id.activity_add_barcode_scan);
        category = (EditText) findViewById(R.id.activity_add_category_add);
        categorySelection = (Spinner) findViewById(R.id.activity_add_category_spinner);
        addItem = (FloatingActionButton) findViewById(R.id.activity_add_fab);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_add_coordinator_layout);
        nameTil = (TextInputLayout) findViewById(R.id.activity_add_name_til);
        barcodeTil = (TextInputLayout) findViewById(R.id.activity_add_barcode_til);
        categoryTil = (TextInputLayout) findViewById(R.id.actiity_add_category_add_til);

        //Init
        category.setText("");
        category.setVisibility(View.INVISIBLE);
        nameTil.setErrorEnabled(true);
        categoryTil.setErrorEnabled(true);

        //On Click Listeners
        scanBarcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeScan();
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processAdd();
            }
        });


    }

    @Override
    public void onResume(){
        super.onResume();

        spinnerList.clear();
        ListDB db = new ListDB(this);
        categoryList = db.getAllCategories();
        spinnerList.add("Create New Category...");
        spinnerList.add("Uncategorized");
        for (ListCategory category : categoryList){
            spinnerList.add(category.getName());
        }

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySelection.setAdapter(spinnerAdapter);
        categorySelection.setSelection(1);
        categorySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //New Item, show category
                    category.setVisibility(View.VISIBLE);
                    categoryTil.setVisibility(View.VISIBLE);
                    newCategoryCreated = true;
                } else if (position == 1) {
                    //No category
                    category.setText("");
                    category.setVisibility(View.INVISIBLE);
                    categoryTil.setVisibility(View.INVISIBLE);
                    newCategoryCreated = false;
                } else {
                    category.setText("");
                    category.setVisibility(View.INVISIBLE);
                    categoryTil.setVisibility(View.INVISIBLE);
                    newCategoryCreated = false;
                    if (position <= 1) {
                        //Error
                        Log.e(StaticReferences.TAG, "Category position exceeded");
                        return;
                    }
                    int itemPos = position - 2;
                    if (itemPos == categoryList.size()) {
                        //Error
                        Log.e(StaticReferences.TAG, "Spinner selection exceeded category list");
                        return;
                    }

                    categorySelected = categoryList.get(itemPos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item_to_db, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, MainPreferences.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void processAdd() {
        //Input Validation
        name.setText(name.getText().toString().trim());
        boolean error = false;
        if (name.getText().toString().equals("")){
            //No name entered (required field)
            nameTil.setError("Required Field. Please enter a name for the item");
            error = true;
        }

        if (newCategoryCreated){
            category.setText(category.getText().toString().trim());
            if (category.getText().toString().equals("")){
                categoryTil.setError("Required Field. Please enter a category name");
                error = true;
            }
        }

        if (error) return;

        //End of Input Validation

        //TODO: Remove test code
        Snackbar.make(coordinatorLayout, "Add item to Database", Snackbar.LENGTH_SHORT)
                .setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }

    private void startBarcodeScan(){
        //Cart Fragment (Scan barcode)
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        if (sp.getBoolean("vision_api_use", true)) {
            int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getApplicationContext());
            if (code == ConnectionResult.SUCCESS) {
                Intent intent = new Intent(this, VisionApiBarcodeCameraActivity.class);
                startActivityForResult(intent, VISION_REQUEST_CODE);
            } else {
                //Fallback to legacy method
                fallbackToOldBarcodeHandling();
            }
        } else {
            fallbackToOldBarcodeHandling();
        }
    }

    private void fallbackToOldBarcodeHandling(){
        isLegacyBarcode = true;
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (!isLegacyBarcode) {
            //Check the request
            if (requestCode == VISION_REQUEST_CODE) {
                //Check if successful
                if (resultCode == RESULT_OK) {
                    //Handle result
                    Barcode barcode = StaticReferences.barcode;
                    StaticReferences.barcode = null;
                    handleBarcode(barcode, null, false);
                    Log.i(StaticReferences.TAG, "Barcode Found: " + barcode.rawValue);
                }
            }
        } else {
            Log.d(StaticReferences.TAG, "Parsing Legacy Barcode data");
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null){
                Log.d(StaticReferences.TAG, "Found valid barcode data");

                LegacyBarcode barcode = new LegacyBarcode(result.getFormatName(), result.getContents());
                barcode.setToString(result.toString());
                handleBarcode(null, barcode, true);
                Log.i(StaticReferences.TAG, "Barcode Found: " + barcode.contents);
                Log.d(StaticReferences.TAG, "Parse Completed");
            }
        }
    }

    private void handleBarcode(Barcode barcode, LegacyBarcode legacyBarcode, boolean isLegacyBarcode){
        if (isLegacyBarcode){
            //Use Legacy barcode
            Log.d(StaticReferences.TAG, "Handling Legacy Barcode...");
            this.barcode.setText(legacyBarcode.contents);
        } else {
            //Use Barcode
            Log.d(StaticReferences.TAG, "Handling GPS Vision Barcode...");
            this.barcode.setText(barcode.rawValue);
        }
    }
}
