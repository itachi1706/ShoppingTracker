package com.itachi1706.shoppingtracker.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itachi1706.shoppingtracker.MainActivityFragment;
import com.itachi1706.shoppingtracker.Objects.CartItem;
import com.itachi1706.shoppingtracker.Objects.ListBase;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.Objects.ListItem;
import com.itachi1706.shoppingtracker.R;
import com.itachi1706.shoppingtracker.utility.CartJsonHelper;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Kenneth on 9/2/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.Adapters
 */
public class ItemListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListBase> items;
    private SharedPreferences sp;
    private Fragment fragment;

    public ItemListRecyclerAdapter(List<ListBase> items, SharedPreferences sp, Fragment fragment) {
        this.items = items;
        this.sp = sp;
        this.fragment = fragment;
    }

    @Override
    public int getItemCount() {return items.size();}

    @Override
    public int getItemViewType(int position){
        if (position == items.size()){
            return 0;
        }
        ListBase item = items.get(position);
        if (item instanceof ListCategory) return 0;
        if (item instanceof ListItem) return 1;
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i){
        ListBase baseItem = items.get(i);
        if (baseItem instanceof ListCategory){
            //Category Binding
            ListCategory cat = (ListCategory) baseItem;
            int childItems = 0;
            if (cat.hasChild()) childItems = cat.getChildProducts().size();
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            categoryViewHolder.category.setText(cat.getName() + " (" + childItems + ")");
        } else if (baseItem instanceof ListItem){
            //Item Binding
            ListItem item = (ListItem) baseItem;
            CategoryItemViewHolder categoryItemViewHolder = (CategoryItemViewHolder) holder;
            categoryItemViewHolder.itemName.setText(item.getName());
            if (item.getBarcode() != null){
                categoryItemViewHolder.itemBarcode.setText(item.getBarcode());
            } else {
                categoryItemViewHolder.itemBarcode.setText("");
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        switch (viewType){
            case 0:
                //Category
                View categoryView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.recyclerview_list_category, viewGroup, false);
                return new CategoryViewHolder(categoryView);
            case 1:
                //Category Items
                View itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.recyclerview_list_items, viewGroup, false);
                return new CategoryItemViewHolder(itemView);
        }
        return null;
    }

    private int getCategoryID(ListCategory category){
        int toAddTo = -1;
        for (int i = 0; i < items.size(); i++){
            if (!(items.get(i) instanceof ListCategory)) continue;
            if (items.get(i) == category){
                toAddTo = i;
            }
        }
        return toAddTo;
    }

    public void addChild(ListCategory category){
        List<ListItem> childItems = category.getChildProducts();

        int toAddTo = getCategoryID(category);
        if (toAddTo == -1) return;

        items.get(toAddTo).setIsExpanded(true);

        for (ListItem item : childItems){
            items.add(toAddTo + 1, item);
            notifyItemInserted(toAddTo + 1);
        }
    }

    public void removeChild(ListCategory category){
        List<ListItem> childItems = category.getChildProducts();
        int toAddTo = getCategoryID(category);
        if (toAddTo == -1) return;

        items.get(toAddTo).setIsExpanded(false);

        for (Iterator<ListBase> iterator = items.iterator(); iterator.hasNext();){
            ListBase baseItem = iterator.next();
            if (baseItem instanceof ListCategory) continue;

            ListItem item = (ListItem) baseItem;
            boolean toRemove = false;
            for (ListItem childItem : childItems){
                if (childItem == item){
                    toRemove = true;
                    break;
                }
            }

            if (toRemove){
                iterator.remove();
                notifyItemRemoved(toAddTo + 1);
            }
        }
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener{

        protected TextView category;

        public CategoryViewHolder(View v){
            super(v);
            category = (TextView) v.findViewById(R.id.category_title);
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getLayoutPosition();
            ListBase item = items.get(position);

            Log.d(StaticReferences.TAG, "Category: " + category.getText() + " isExpanded: " + item.isExpanded());

            if (item.isExpanded()){
                removeChild((ListCategory) item);
            } else {
                ListCategory itemCat = (ListCategory) item;
                if (itemCat.hasChild()){
                    addChild(itemCat);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = this.getLayoutPosition();
            ListBase item = items.get(position);
            if (item instanceof ListItem) return;

            final ListCategory categoryItem = (ListCategory) item;

            menu.setHeaderTitle("Actions for " + categoryItem.getName());
            menu.add(0, v.getId(), 0, "Edit Name").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (fragment instanceof MainActivityFragment) {
                        MainActivityFragment f = (MainActivityFragment) fragment;
                        f.updateCategory(categoryItem);
                    }
                    return true;
                }
            });
            menu.add(0, v.getId(), 1, "Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (fragment instanceof MainActivityFragment) {
                        MainActivityFragment f = (MainActivityFragment) fragment;
                        f.deleteCategory(categoryItem);
                    }
                    return true;
                }
            });
        }
    }

    public class CategoryItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener{

        protected TextView itemName;
        protected TextView itemBarcode;

        public CategoryItemViewHolder(View v){
            super(v);
            itemName = (TextView) v.findViewById(R.id.item_title);
            itemBarcode = (TextView) v.findViewById(R.id.item_barcode);
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getLayoutPosition();
            final ListBase item = items.get(position);
            final View finalV = v;

            Log.d(StaticReferences.TAG, "Item: " + itemName.getText() + " barcode: " + itemBarcode.getText() + " Category ID: " + ((ListItem) item).getCategory());

            //Add item
            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(((ListItem) item).getName());
            LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_add_item_to_cart, null);

            final EditText qty = (EditText) view.findViewById(R.id.dialog_quantity);
            final EditText price = (EditText) view.findViewById(R.id.dialog_price);
            builder.setView(view);
            builder.setCancelable(false);
            builder.setPositiveButton("Add to Cart", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String quantity = qty.getText().toString();
                    String itemPrice = price.getText().toString();
                    quantity = quantity.trim();
                    itemPrice = itemPrice.trim();
                    if (quantity.equals("") || itemPrice.equals("")) {
                        Toast.makeText(finalV.getContext(), "You need to enter a price and quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final CartItem cartItem = new CartItem(((ListItem) item).getId(), Integer.parseInt(qty.getText().toString()), Double.parseDouble(price.getText().toString()), (ListItem) item);
                    CartJsonHelper.addCartItemToJsonCart(cartItem, sp);
                    if (fragment instanceof MainActivityFragment){
                        MainActivityFragment f = (MainActivityFragment) fragment;
                        f.cartItemAdded(cartItem);
                    }
                }
            });
            builder.setNeutralButton(android.R.string.cancel, null);
            builder.show();

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = this.getLayoutPosition();
            ListBase item = items.get(position);
            if (item instanceof ListCategory) return;

            final ListItem listItem = (ListItem) item;

            menu.setHeaderTitle("Actions for " + listItem.getName());
            menu.add(0, v.getId(), 0, "Edit Item").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (fragment instanceof MainActivityFragment) {
                        MainActivityFragment f = (MainActivityFragment) fragment;
                        f.updateItem(listItem);
                    }
                    return true;
                }
            });
            menu.add(0, v.getId(), 1, "Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (fragment instanceof MainActivityFragment) {
                        MainActivityFragment f = (MainActivityFragment) fragment;
                        f.deleteItem(listItem);
                    }
                    return true;
                }
            });
        }
    }
}
