package com.itachi1706.shoppingtracker.Adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itachi1706.shoppingtracker.HistoryFragment;
import com.itachi1706.shoppingtracker.Objects.HistoryItem;
import com.itachi1706.shoppingtracker.R;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        DecimalFormat df = new DecimalFormat("0.00");
        holder.histDateTime.setText("Cart on " + d.toString());
        holder.histTotal.setText("Total Spent: $" + df.format(item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected TextView histDateTime;
        protected TextView histTotal;

        public HistoryViewHolder(View v) {
            super(v);

            histDateTime = (TextView) v.findViewById(R.id.item_title);
            histTotal = (TextView) v.findViewById(R.id.item_barcode);
            v.setOnClickListener(this);
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
    }
}
