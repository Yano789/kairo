package com.example.infosys_1d;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TimetableViewHolder extends RecyclerView.ViewHolder {
    public final TextView timeTextView;
    public final FrameLayout[] dayFrameLayouts = new FrameLayout[7];


    public TimetableViewHolder(@NonNull View itemView) {
        super(itemView);
        timeTextView = itemView.findViewById(R.id.time);
        dayFrameLayouts[0] = itemView.findViewById(R.id.mon);
        dayFrameLayouts[1] = itemView.findViewById(R.id.tue);
        dayFrameLayouts[2] = itemView.findViewById(R.id.wed);
        dayFrameLayouts[3] = itemView.findViewById(R.id.thu);
        dayFrameLayouts[4] = itemView.findViewById(R.id.fri);
        dayFrameLayouts[5] = itemView.findViewById(R.id.sat);
        dayFrameLayouts[6] = itemView.findViewById(R.id.sun);
    }
}
