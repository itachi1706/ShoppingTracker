package com.itachi1706.shoppingtracker.CustomPreferenceObject;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Kenneth on 9/8/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.CustomPreferenceObject
 */
public class EditTaxPreference extends android.preference.EditTextPreference {

    public EditTaxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult){
        super.onDialogClosed(positiveResult);

        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary(){
        return this.getText() + "%";
    }
}
