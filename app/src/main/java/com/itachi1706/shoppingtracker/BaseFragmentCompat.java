package com.itachi1706.shoppingtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itachi1706.shoppingtracker.Interfaces.OnRefreshListener;

/**
 * Created by Kenneth on 8/10/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker
 */
public abstract class BaseFragmentCompat extends Fragment implements OnRefreshListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(getFragmentLayout(), container, false);
    }

    protected abstract int getFragmentLayout();

}
