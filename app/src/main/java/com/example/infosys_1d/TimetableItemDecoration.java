package com.example.infosys_1d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class TimetableItemDecoration extends RecyclerView.ItemDecoration {
    private final Paint linePaint;
    private final Paint textPaint;
    private final float pxPerMinute;

    public TimetableItemDecoration(Context context) {
        // Paint for lines (horizontal and vertical)
        linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(context, R.color.grey));
        linePaint.setStrokeWidth(1.5f);
        linePaint.setAntiAlias(false);

        // Paint for time text
        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(context, R.color.dark_grey));
        textPaint.setTextSize(32); // Adjust font size for your scale
        textPaint.setAntiAlias(true); // Smooth edges

        pxPerMinute = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1.5f, context.getResources().getDisplayMetrics()
        );
    }


    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();

        // Calculate column width using RecyclerView's full width
        float totalWidth = parent.getWidth();
        float columnWidth = totalWidth / 8f; // 1 time column + 7 days

        // Get how much the RecyclerView has scrolled vertically
        int scrollY = parent.computeVerticalScrollOffset();

        // Draw vertical lines (between columns)
        for (int j = 1; j < 8; j++) {
            float x = j * columnWidth;
            x = Math.round(x); // Align to nearest whole pixel
            canvas.drawLine(x, 0, x, parent.getHeight(), linePaint);
        }

        int hourCount = 24;
        float startX = columnWidth * 0.9f;
        float endX = columnWidth * 8f;
        float textX = columnWidth * 0.1f;

        for (int hour = 1; hour < hourCount; hour++) {
            float y = hour * 60 * pxPerMinute - scrollY;

            // Draw horizontal line
            canvas.drawLine(startX, Math.round(y), endX, Math.round(y), linePaint);

            // Draw time text centered on the line
            float textHeight = textPaint.descent() - textPaint.ascent();
            float textY = y + textHeight / 2 - textPaint.descent();

            String label = String.format("%02d:00", hour);
            canvas.drawText(label, textX, textY, textPaint);
        }
    }

}

