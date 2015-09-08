package com.itachi1706.shoppingtracker.Adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itachi1706.shoppingtracker.HistoryFragment;
import com.itachi1706.shoppingtracker.Objects.HistoryItem;
import com.itachi1706.shoppingtracker.R;
import com.itachi1706.shoppingtracker.utility.StaticMethods;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kenneth on 9/7/2015.
 * for ShoppingTracker in package com.itachi1706.shoppingtracker.Adapters
 */
public class HistoryListRecyclerAdapter extends RecyclerView.Adapter<HistoryListRecyclerAdapter.HistoryViewHolder> {

    private List<HistoryItem> items;
    private Fragment fragment;

    public HistoryListRecyclerAdapter(List<HistoryItem> list, Fragment fragment){
        this.items = list;
        this.fragment = fragment;
    }

    public HistoryListRecyclerAdapter(HistoryItem[] items, Fragment fragment){
        this.items = Arrays.asList(items);
        this.fragment = fragment;
    }


    @Override
    public HistoryListRecyclerAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list_items, parent, false);
        return new HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryListRecyclerAdapter.HistoryViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        Date d = new Date(item.getDate());
        DateFormat dateFormat = new SimpleDateFormat("E dd-MMM-yyyy hh:mm a", Locale.US);

        DecimalFormat df = new DecimalFormat("0.00");
        holder.histDateTime.setText("Cart on " + dateFormat.format(d));
        holder.histTotal.setText("Total Spent: " + StaticMethods.getPriceSymbol() + df.format(item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{

        protected TextView histDateTime;
        protected TextView histTotal;

        public HistoryViewHolder(View v) {
            super(v);

            histDateTime = (TextView) v.findViewById(R.id.item_title);
            histTotal = (TextView) v.findViewById(R.id.item_barcode);
            v.setOnClickListener(this);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getLayoutPosition();
            HistoryItem item = items.get(position);

            if (fragment instanceof HistoryFragment){
                HistoryFragment frag = (HistoryFragment) fragment;
                frag.selectHistoryItem(item);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = this.getLayoutPosition();
            final HistoryItem histItem = items.get(position);

            if (!(fragment instanceof HistoryFragment)) return;

            menu.add(0, v.getId(), 0, "Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (fragment instanceof HistoryFragment) {
                        HistoryFragment f = (HistoryFragment) fragment;
                        f.deleteHistoryFile(histItem);
                    }
                    return true;
                }
            });
        }
    }
}
