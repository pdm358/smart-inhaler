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

    // Cached copy of InhalerUsageEvents
    private List<InhalerUsageEvent> iueEntries;

    // Inflater
    private final LayoutInflater iueInflater;

    // Logging
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public IUEListAdapter(Context context) { iueInflater = LayoutInflater.from(context); }

    @NonNull
    @Override
    public IUEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = iueInflater.inflate(R.layout.iue_item, parent, false);
        return new IUEViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull IUEViewHolder holder, int position) {
        if (iueEntries != null) {
            // Will probably use a custom set method in the future, so I abstracted that into the holder.
            holder.updateView(iueEntries.get(position));
        }
        else {
            // covers the case of data not being ready yet
            holder.iueItemView.setText("- - -");
        }
    }

    void setWords(List<InhalerUsageEvent> inhalerUsageEvents) {
        iueEntries = inhalerUsageEvents;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return iueEntries != null ? iueEntries.size() : 0;
    }

    // Class that holds View information for displaying one item from the item's layout
    static class IUEViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        // we may want to change TextView into something that displays the IUE's in a better way
        public final TextView iueItemView;
        final IUEListAdapter iueListAdapter;

        public IUEViewHolder(@NonNull View itemView, IUEListAdapter adapter) {
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

        /**
         * Updates the view using {@code obj}.
         * @param obj
         */
        public void updateView(Object obj){

            iueItemView.setText((String) obj);

        }
    }
}
