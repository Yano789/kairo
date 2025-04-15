package com.example.infosys_1d.Schedule;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.Event.Event;
import com.example.infosys_1d.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class EventCanvasView extends View {
    private List<Event> eventList = new ArrayList<>();
    private LocalDate currentWeekStart;
    private int rowHeightPx = 0;

    private final Paint boxPaint;
    private final TextPaint textPaint;

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private OnEventClickListener eventClickListener;

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.eventClickListener = listener;
    }

    public EventCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        boxPaint = new Paint();
        boxPaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);

        setWillNotDraw(false);
    }

    public void setData(List<Event> events, LocalDate weekStart, int rowHeight) {
        this.eventList = events;
        this.currentWeekStart = weekStart;
        this.rowHeightPx = rowHeight;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (eventList == null || rowHeightPx <= 0 || currentWeekStart == null) return;

        float pxPerMin = rowHeightPx / 5f;
        float colWidth = getWidth() / 8f;

        View root = getRootView();
        RecyclerView recycler = root.findViewById(R.id.timetableRecyclerView);
        if (recycler == null) return;

        int scrollOffsetY = recycler.computeVerticalScrollOffset();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        for (Event event : eventList) {
            LocalDate eventDate = LocalDate.parse(event.getDate());
            int dayIndex = (int) ChronoUnit.DAYS.between(currentWeekStart, eventDate);
            if (dayIndex < 0 || dayIndex >= 7) continue;

            // Convert milliseconds to minutes since midnight
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            cal.setTimeInMillis(event.getStartTime());
            int startMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
            cal.setTimeInMillis(event.getEndTime());
            int endMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

            if (startMin >= endMin) continue;

            float left = colWidth * (dayIndex + 1);
            float right = left + colWidth;
            float top = startMin * pxPerMin - scrollOffsetY;
            float bottom = endMin * pxPerMin - scrollOffsetY;

            // Draw only if visible
            if (bottom < 0 || top > getHeight()) continue;

            // Set event color
            try {
                boxPaint.setColor(ContextCompat.getColor(getContext(), event.getColor()));
            } catch (Exception e) {
                boxPaint.setColor(ContextCompat.getColor(getContext(), R.color.light_blue));
            }

            canvas.drawRect(left, top, right, bottom, boxPaint);

            String displayText = event.getTitle() + "\n" +
                    timeFormat.format(event.getStartTime()) + " - " +
                    timeFormat.format(event.getEndTime());

            StaticLayout layout = StaticLayout.Builder
                    .obtain(displayText, 0, displayText.length(), textPaint, (int) (colWidth - 16))
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0, 1)
                    .setIncludePad(false)
                    .build();

            canvas.save();
            canvas.translate(left + 8, top + 8);
            float maxHeight = bottom - top - 16;
            if (layout.getHeight() > maxHeight) {
                canvas.clipRect(0, 0, colWidth - 16, maxHeight);
            }
            layout.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;

        float x = event.getX();
        float y = event.getY();

        float colWidth = getWidth() / 8f;
        float pxPerMin = rowHeightPx / 5f;

        View root = getRootView();
        RecyclerView recycler = root.findViewById(R.id.timetableRecyclerView);
        if (recycler == null) return false;

        int scrollOffsetY = recycler.computeVerticalScrollOffset();
        float adjustedY = y + scrollOffsetY;

        for (Event e : eventList) {
            LocalDate eventDate = LocalDate.parse(e.getDate());
            int dayIndex = (int) ChronoUnit.DAYS.between(currentWeekStart, eventDate);
            if (dayIndex < 0 || dayIndex >= 7) continue;

            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"));
            cal.setTimeInMillis(e.getStartTime());
            int startMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
            cal.setTimeInMillis(e.getEndTime());
            int endMin = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

            if (startMin >= endMin) continue;

            float left = colWidth * (dayIndex + 1);
            float right = left + colWidth;
            float top = pxPerMin * startMin;
            float bottom = pxPerMin * endMin;

            if (adjustedY >= top && adjustedY <= bottom && x >= left && x <= right) {
                if (eventClickListener != null) {
                    eventClickListener.onEventClick(e);
                }
                return true;
            }
        }

        return false;
    }

    private String formatTime(int minutes) {
        int hour = minutes / 60;
        int min = minutes % 60;
        return String.format("%02d:%02d", hour, min);
    }
}