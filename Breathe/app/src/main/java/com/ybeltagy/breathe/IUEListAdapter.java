package com.ybeltagy.breathe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.LinkedList;

public class IUEListAdapter
        extends RecyclerView.Adapter<IUEListAdapter.IUEViewHolder> {

    // Placeholder list
    // -> TODO: to be replaced with data source of inhaler usage event (IUE) objects
    private final LinkedList<String> iueEntries;
    // Inflater
    private LayoutInflater iueInflater;

    public IUEListAdapter(Context context, LinkedList<String> iueEntryList) {
        iueInflater = LayoutInflater.from(context);
        this.iueEntries = iueEntryList;
    }

    @NonNull
    @Override
    public IUEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = iueInflater.inflate(R.layout.wordlist_item, parent, false);
        return new IUEViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull IUEViewHolder holder, int position) {
        String current = iueEntries.get(position);
        holder.iueItemView.setText(current);

    }

    @Override
    public int getItemCount() {
        return iueEntries.size();
    }


    // Class that holds View information for displaying one item from the item's layout
    class IUEViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener{
        // may want to change TextView into something that displays the IUE's in a better way
        public final TextView iueItemView;
        final IUEListAdapter iueListAdapter;

        public IUEViewHolder(@NonNull View itemView, IUEListAdapter adapter) {
            super(itemView);
            iueItemView = itemView.findViewById(R.id.word);
            this.iueListAdapter = adapter;

            iueItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // get the position of the item that was clicked
            int position = getLayoutPosition();
            // use that to access the affected item in mWordList
            String element = iueEntries.get(position);
            // change the word in the mWordList
            iueEntries.set(position, "Clicked! " + element);
            // notify the adapter, that the data has changed -> update the RecyclerView to display
            // the new data
            iueListAdapter.notifyDataSetChanged();
        }
    }
}
