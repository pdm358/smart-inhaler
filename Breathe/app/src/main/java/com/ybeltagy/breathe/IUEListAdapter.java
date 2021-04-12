package com.ybeltagy.breathe;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * This class prepares and updates the IUE data to be displayed in the RecyclerView in the Main
 * Activity.
 */
public class IUEListAdapter
        extends RecyclerView.Adapter<IUEListAdapter.IUEViewHolder> {

    // Inflater
    private final LayoutInflater iueInflater;
    // Cached copy of InhalerUsageEvents
    private List<InhalerUsageEvent> iueEntries;

    // Logging
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    IUEListAdapter(Context context) { iueInflater = LayoutInflater.from(context); }

    @Override
    public IUEViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = iueInflater.inflate(R.layout.iue_item, parent, false);
        return new IUEViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(IUEViewHolder holder, int position) {
//        if (iueEntries != null) {
            // Will probably use a custom set method in the future, so I abstracted that into the holder.
            InhalerUsageEvent current = iueEntries.get(position);
            Log.d("IUEListAdapter", "setting text to "
                    + current.getInhalerUsageEventTimeStamp().toString());
            holder.iueItemView.setText(current.getInhalerUsageEventTimeStamp().toString());
//        }
//        else {
//            // covers the case of data not being ready yet
//            holder.iueItemView.setText("- - -");
//        }
    }

    void setInhalerUsageEvents(List<InhalerUsageEvent> inhalerUsageEvents) {
        iueEntries = inhalerUsageEvents;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // debug
        int size = iueEntries != null ? iueEntries.size() : 0;
        Log.d("IUEListAdapter", "iue entries size = " + size);
        return iueEntries != null ? iueEntries.size() : 0;
    }

    // Class that holds View information for displaying one item from the item's layout
    static class IUEViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        // we may want to change TextView into something that displays the IUE's in a better way
        public final TextView iueItemView;
        final IUEListAdapter iueListAdapter;

        public IUEViewHolder(View itemView, IUEListAdapter adapter) {
            super(itemView);
            iueItemView = itemView.findViewById(R.id.iue_textview);
            this.iueListAdapter = adapter;

            iueItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // In the future, we might want to start an activity for results.
            Intent intent = new Intent(this.iueItemView.getContext(), DiaryEntryActivity.class);
            this.iueItemView.getContext().startActivity(intent);

            //todo delete later
            Log.d(LOG_TAG, "Diary entry activity launched!");
        }
    }
}
