package com.itachi1706.shoppingtracker.Adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itachi1706.shoppingtracker.Objects.ListBase;
import com.itachi1706.shoppingtracker.Objects.ListCategory;
import com.itachi1706.shoppingtracker.Objects.ListItem;
import com.itachi1706.shoppingtracker.R;
import com.itachi1706.shoppingtracker.utility.StaticReferences;

import java.util.List;

/**
 * Created by Kenneth on 9/2/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.Adapters
 */
public class ItemListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListBase> items;

    public ItemListRecyclerAdapter(List<ListBase> items) {this.items = items;}

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
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            categoryViewHolder.category.setText(cat.getName());
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

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected TextView category;

        public CategoryViewHolder(View v){
            super(v);
            category = (TextView) v.findViewById(R.id.category_title);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Category: " + category.getText(), Snackbar.LENGTH_SHORT).show();
            Log.d(StaticReferences.TAG, "Category: " + category.getText());
        }
    }

    public class CategoryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected TextView itemName;
        protected TextView itemBarcode;

        public CategoryItemViewHolder(View v){
            super(v);
            itemName = (TextView) v.findViewById(R.id.item_title);
            itemBarcode = (TextView) v.findViewById(R.id.item_barcode);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Item: " + itemName.getText() + " barcode: " + itemBarcode.getText(), Snackbar.LENGTH_SHORT).show();
            Log.d(StaticReferences.TAG, "Item: " + itemName.getText() + " barcode: " + itemBarcode.getText());
        }
    }
}
