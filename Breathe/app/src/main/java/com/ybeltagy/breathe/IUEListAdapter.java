package com.ybeltagy.breathe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

    private static ClickListener clickListener;

    // Logging
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    public IUEListAdapter(Context context) {
        iueInflater = LayoutInflater.from(context);
    }

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
            InhalerUsageEvent current = iueEntries.get(position);
            Log.d("IUEListAdapter", "setting text to "
                    + current.getInhalerUsageEventTimeStamp().toString());
            // show timestamp
            holder.iueItemView.setText(current.getInhalerUsageEventTimeStamp().toString());
            // show diary entry message for this inhaler usage event
            if (current.getDiaryEntry() != null && current.getDiaryEntry().getMessage() != null) {
                holder.diaryMessage.setText(current.getDiaryEntry().getMessage());
            }

            // show tag
            if (current.getDiaryEntry() != null && current.getDiaryEntry().getTag() != null) {
                holder.diaryMessage.setText(String.format("Tagged as : %s",
                        current.getDiaryEntry().getTag().toString()));
            }
        } else {
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

    /**
     * Identifies which InhalerUsageEvent was clicked for methods that handle user events
     *
     * @param position Position of InhalerUsageEvent in RecyclerView
     * @return The InhalerUsageEvent at the input position
     */
    public InhalerUsageEvent getInhalerUsageEventAtPosition(int position) {
        return iueEntries.get(position);
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        IUEListAdapter.clickListener = clickListener;
    }

    // Class that holds View information for displaying one item from the item's layout
    static class IUEViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout inhalerUsageEventEntry; // the entire entry in the timeline
        public final TextView iueItemView;  // text view of the timestamp
        public final TextView diaryMessage; // text view of the diary entry
        final IUEListAdapter iueListAdapter;

        public IUEViewHolder(@NonNull View itemView, IUEListAdapter adapter) {
            super(itemView);
            inhalerUsageEventEntry = itemView.findViewById(R.id.inhaler_usage_event_entry);
            iueItemView = itemView.findViewById(R.id.timestamp_textview);
            diaryMessage = itemView.findViewById(R.id.diary_entry);
            this.iueListAdapter = adapter;

            inhalerUsageEventEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAbsoluteAdapterPosition());
                }
            });
        }
    }
}
