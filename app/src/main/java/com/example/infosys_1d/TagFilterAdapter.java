package com.example.infosys_1d;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.util.ArrayList;
import java.util.List;

public class TagFilterAdapter extends BaseAdapter {

    private Context context;
    private List<String> allTags;  // Full list of tags
    private List<String> filteredTags;  // Filtered list based on search
    private boolean[] checkedStates;

    public TagFilterAdapter(Context context, List<String> tags) {
        this.context = context;
        this.allTags = new ArrayList<>(tags);
        this.filteredTags = new ArrayList<>(tags);  // Initially, all tags are shown
        this.checkedStates = new boolean[tags.size()];
    }

    @Override
    public int getCount() {
        return filteredTags.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        }

        CheckedTextView checkedTextView = (CheckedTextView) convertView;  // Use CheckedTextView
        String tag = filteredTags.get(position);
        checkedTextView.setText(tag);

        // Find the index of this tag in the original list to get its checked state
        int originalIndex = allTags.indexOf(tag);
        checkedTextView.setChecked(checkedStates[originalIndex]);

        // Handle click to toggle the checked state
        checkedTextView.setOnClickListener(v -> {
            checkedStates[originalIndex] = !checkedStates[originalIndex];
            checkedTextView.setChecked(checkedStates[originalIndex]);
        });

        return convertView;
    }

    public void setCheckedStates(boolean[] states) {
        this.checkedStates = states.clone();
        notifyDataSetChanged();
    }

    public boolean[] getCheckedStates() {
        return checkedStates.clone();
    }

    public void checkAll() {
        for (int i = 0; i < checkedStates.length; i++) {
            checkedStates[i] = true;
        }
        notifyDataSetChanged();
    }

    public void removeAll() {
        for (int i = 0; i < checkedStates.length; i++) {
            checkedStates[i] = false;
        }
        notifyDataSetChanged();
    }

    // Filter tags based on search query
    public void filter(String query) {
        filteredTags.clear();
        if (query.isEmpty()) {
            filteredTags.addAll(allTags);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (String tag : allTags) {
                if (tag.toLowerCase().contains(lowerCaseQuery)) {
                    filteredTags.add(tag);
                }
            }
        }
        notifyDataSetChanged();
    }
}