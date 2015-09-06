package com.itachi1706.shoppingtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.itachi1706.shoppingtracker.Adapters.ItemListRecyclerAdapter;
import com.itachi1706.shoppingtracker.Adapters.StringRecyclerAdapter;
import com.itachi1706.shoppingtracker.Database.ListDB;
import com.itachi1706.shoppingtracker.Interfaces.OnRefreshListener;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.ListBase;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.Objects.ListItem;
import com.itachi1706.shoppingtracker.utility.CartJsonHelper;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnRefreshListener {

    public MainActivityFragment() {
    }

    View v;
    SharedPreferences sp;

    RecyclerView recyclerView;

    private final int UPDATE_ITEM_REQUEST_CODE = 102;

    //From Main Activity
    CardView alwaysHideThisView;
    FloatingActionButton fab;

    //No Items
    String[] noItems = {"No Items found. Add some items now!"};
    StringRecyclerAdapter noItemsadapter;

    //Has Items Adapter
    ItemListRecyclerAdapter adapter;
    ListDB db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main_screen, container, false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        //No Items
        noItemsadapter = new StringRecyclerAdapter(noItems);

        alwaysHideThisView = (CardView) getActivity().findViewById(R.id.card_view);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.activity_fab);

        recyclerView = (RecyclerView) v.findViewById(R.id.rv_items);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        db = new ListDB(getActivity());

        onSwipeRefresh();

        return v;
    }

    @Override
    public void onRefresh() {
        checkAndUpdateAdapter();
    }

    @Override
    public void onSwipeRefresh(){
        if (StaticReferences.isMainSwiped || StaticReferences.isFirstLaunched) {
            Log.d(StaticReferences.TAG, "Main Fragment triggered");
            if (fab != null)
                fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.plus_48));
            if (alwaysHideThisView != null)
                alwaysHideThisView.setVisibility(View.GONE);
            StaticReferences.isMainSwiped = false;
            StaticReferences.isFirstLaunched = false;
        }

        onRefresh();
    }

    @Override
    public void cartItemAdded(final CartItem cartItem) {
        CoordinatorLayout layout = (CoordinatorLayout) getActivity().findViewById(R.id.activity_coordinator_layout);
        Snackbar.make(layout, "Added to Cart", Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CartJsonHelper.removeCartItemFromJsonCart(cartItem, sp);
                    }
                }).show();
    }

    @Override
    public void deleteCategory(final ListCategory category) {
        new AlertDialog.Builder(getActivity()).setTitle("Are you sure?")
                .setMessage("Are you sure you want to delete this category? This will not be reversible!" +
                        " \n(This will also delete any items that are in this category)")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteCategory(category);
                        onRefresh();
                        Snackbar.make(getActivity().findViewById(R.id.activity_coordinator_layout), "Category deleted", Snackbar.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    @Override
    public void updateCategory(final ListCategory newCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rename " + newCategory.getName());
        builder.setMessage("Edit name for " + newCategory.getName() + " here");
        final EditText renamer = new EditText(getActivity());
        renamer.setText(newCategory.getName());
        builder.setView(renamer);
        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (renamer.getText().toString().equals("")){
                    Snackbar.make(getActivity().findViewById(R.id.activity_coordinator_layout), "New name cannot be empty", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                ListCategory updatedCategory = new ListCategory(newCategory.getId(), renamer.getText().toString());
                db.updateCategory(updatedCategory);
                onRefresh();
                Snackbar.make(getActivity().findViewById(R.id.activity_coordinator_layout), "Category Renamed", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.updateCategory(newCategory);
                                onRefresh();
                            }
                        }).show();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    @Override
    public void deleteItem(final ListItem item) {
        new AlertDialog.Builder(getActivity()).setTitle("Are you sure?")
                .setMessage("Are you sure you want to delete this item? This will not be reversible!")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteProduct(item);
                        onRefresh();
                        Snackbar.make(getActivity().findViewById(R.id.activity_coordinator_layout), "Item deleted", Snackbar.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
    }

    @Override
    public void updateItem(ListItem item) {
        //TODO: Figure out how I'm gonna update items
        Intent updateItemIntent = new Intent(getActivity(), AddItemToDB.class);
        updateItemIntent.putExtra("update", true);
        updateItemIntent.putExtra("itemID", item.getId());
        startActivityForResult(updateItemIntent, UPDATE_ITEM_REQUEST_CODE);
    }

    @Override
    public void cartItemClicked(CartItem item) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(StaticReferences.TAG, "Main Activity Fragment receive request code: " + requestCode);

        if (requestCode == UPDATE_ITEM_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final ListItem revertItem = StaticReferences.updateItemTmp;

            if (revertItem != null) {
                Log.d(StaticReferences.TAG, "Present update item with undo option");
                Snackbar.make(getActivity().findViewById(R.id.activity_coordinator_layout), "Item Updated Successfully", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.i(StaticReferences.TAG, "Reverting update");
                                db.updateProduct(revertItem);
                                onRefresh();
                            }
                        }).show();
            } else {
                Log.d(StaticReferences.TAG, "Present update item without undo option");
                Snackbar.make(getActivity().findViewById(R.id.activity_coordinator_layout), "Item Updated Successfully", Snackbar.LENGTH_SHORT).show();
            }

            onRefresh();
            StaticReferences.updateItemTmp = null;
        }
    }

    private void checkAndUpdateAdapter() {
        if (db.isEmptyItems()) {
            //Null
            recyclerView.setAdapter(noItemsadapter);
        } else {
            //All Items
            ArrayList<ListCategory> categories = db.getAllCategories();
            //Uncatergorized items
            ArrayList<ListItem> items = db.getAllUncategorizedItems();

            for (ListCategory category : categories) {
                category.setChildProducts(db.getAllItemsByCategory(category));
            }

            List<ListBase> baseItems = mergeCatAndItems(categories, items);
            adapter = new ItemListRecyclerAdapter(baseItems, sp, this);
            recyclerView.setAdapter(adapter);
        }
    }

    private List<ListBase> mergeCatAndItems(ArrayList<ListCategory> categories, ArrayList<ListItem> items){
        List<ListBase> result = new ArrayList<>();
        for (ListCategory category : categories){
            result.add(category);
        }
        for (ListItem item : items){
            result.add(item);
        }

        return result;
    }
}
