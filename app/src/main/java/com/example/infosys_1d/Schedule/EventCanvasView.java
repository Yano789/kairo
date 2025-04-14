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
import androidx.recyclerview.widget.RecyclerView;

import com.example.infosys_1d.R;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EventCanvasView extends View {
    private List<MyEvent> eventList = new ArrayList<>();
    private LocalDate currentWeekStart;
    private int rowHeightPx = 0;

    private final Paint boxPaint;
    private final TextPaint textPaint;

    public interface OnEventClickListener {
        void onEventClick(MyEvent event);
    }

    private OnEventClickListener eventClickListener;

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.eventClickListener = listener;
    }

    public EventCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        boxPaint = new Paint();
        boxPaint.setColor(0xFFFFF3A0); // light yellow
        boxPaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(28f);
        textPaint.setAntiAlias(true);

        setWillNotDraw(false);
    }

    public void setData(List<MyEvent> events, LocalDate weekStart, int rowHeight) {
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
        if (recycler == null || recycler.getLayoutManager() == null) return;
        View rowZero = recycler.getLayoutManager().findViewByPosition(0);

        float offsetY = (rowZero != null) ? rowZero.getTop() : 0;

        for (MyEvent event : eventList) {
            int dayIndex = (int) ChronoUnit.DAYS.between(currentWeekStart, event.getDate());
            if (dayIndex < 0 || dayIndex >= 7) continue;

            float left = colWidth * (dayIndex + 1);
            float right = left + colWidth;

            float top = event.getStartTime() * pxPerMin - offsetY;
            float bottom = event.getEndTime() * pxPerMin - offsetY;

            canvas.drawRect(left, top, right, bottom, boxPaint);

            String displayText = event.getTitle() + "\n" +
                    formatTime(event.getStartTime()) + " - " + formatTime(event.getEndTime());

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

    private String formatTime(int minutes) {
        int hour = minutes / 60;
        int min = minutes % 60;
        return String.format("%02d:%02d", hour, min);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;

        float x = event.getX();
        float y = event.getY();

        float colWidth = getWidth() / 8f;
        float pxPerMin = rowHeightPx / 5f;

        for (MyEvent e : eventList) {
            int dayIndex = (int) ChronoUnit.DAYS.between(currentWeekStart, e.getDate());
            float left = colWidth * (dayIndex + 1);
            float right = left + colWidth;
            float top = pxPerMin * e.getStartTime();
            float bottom = pxPerMin * e.getEndTime();

            RectF rect = new RectF(left, top, right, bottom);
            if (rect.contains(x, y)) {
                if (eventClickListener != null) {
                    eventClickListener.onEventClick(e);
                }
                return true;
            }
        }
        return false;
    }
}


//package com.example.infosys_1d.Schedule;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.RectF;
//import android.text.Layout;
//import android.text.StaticLayout;
//import android.text.TextPaint;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.infosys_1d.R;
//
//import java.time.LocalDate;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.List;
//
//public class EventCanvasView extends View {
//    private List<MyEvent> eventList = new ArrayList<>();
//    private LocalDate currentWeekStart;
//    private int rowHeightPx = 0;
//
//    private final Paint boxPaint;
//    private final TextPaint textPaint;
//
//    public EventCanvasView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//
//        boxPaint = new Paint();
//        boxPaint.setColor(0xFFFFF3A0); // light yellow
//        boxPaint.setStyle(Paint.Style.FILL);
//
//        textPaint = new TextPaint();
//        textPaint.setColor(Color.BLACK);
//        textPaint.setTextSize(28f);
//        textPaint.setAntiAlias(true);
//
//        setWillNotDraw(false);
//    }
//
//    public void setData(List<MyEvent> events, LocalDate weekStart, int rowHeight) {
//        this.eventList = events;
//        this.currentWeekStart = weekStart;
//        this.rowHeightPx = rowHeight;
//        invalidate();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (eventList == null || rowHeightPx <= 0 || currentWeekStart == null) return;
//
//        float pxPerMin = rowHeightPx / 5f;
//        float colWidth = getWidth() / 8f;
//
//        View root = getRootView();
//        RecyclerView recycler = root.findViewById(R.id.timetableRecyclerView);
//        if (recycler == null || recycler.getLayoutManager() == null) return;
//        View rowZero = recycler.getLayoutManager().findViewByPosition(0);
//
//
//        float offsetY = (rowZero != null) ? rowZero.getTop() : 0;
//
//        for (MyEvent event : eventList) {
//            int dayIndex = (int) ChronoUnit.DAYS.between(currentWeekStart, event.getDate());
//            if (dayIndex < 0 || dayIndex >= 7) continue;
//
//            float left = colWidth * (dayIndex + 1);
//            float right = left + colWidth;
//
//            float top = event.getStartTime() * pxPerMin - offsetY;
//            float bottom = event.getEndTime() * pxPerMin - offsetY;
//
//            // Draw box
//            canvas.drawRect(left, top, right, bottom, boxPaint);
//
//            // Multiline text
//            String displayText = event.getName() + "\n" +
//                    formatTime(event.getStartTime()) + " - " + formatTime(event.getEndTime());
//
//            StaticLayout layout = StaticLayout.Builder
//                    .obtain(displayText, 0, displayText.length(), textPaint, (int) (colWidth - 16))
//                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
//                    .setLineSpacing(0, 1)
//                    .setIncludePad(false)
//                    .build();
//
//            canvas.save();
//            canvas.translate(left + 8, top + 8);
//            float maxHeight = bottom - top - 16;
//            if (layout.getHeight() > maxHeight) {
//                canvas.clipRect(0, 0, colWidth - 16, maxHeight);
//            }
//            layout.draw(canvas);
//            canvas.restore();
//        }
//    }
//
//
//    private String formatTime(int minutes) {
//        int hour = minutes / 60;
//        int min = minutes % 60;
//        return String.format("%02d:%02d", hour, min);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;
//
//        float x = event.getX();
//        float y = event.getY();
//
//        float colWidth = getWidth() / 8f;
//        float pxPerMin = rowHeightPx / 5f;
//
//        for (MyEvent e : eventList) {
//            int dayIndex = (int) ChronoUnit.DAYS.between(currentWeekStart, e.getDate());
//            float left = colWidth * (dayIndex + 1);
//            float right = left + colWidth;
//            float top = pxPerMin * e.getStartTime();
//            float bottom = pxPerMin * e.getEndTime();
//
//            RectF rect = new RectF(left, top, right, bottom);
//            if (rect.contains(x, y)) {
//                Toast.makeText(getContext(),
//                        e.getName() + "\n" + formatTime(e.getStartTime()) + " - " + formatTime(e.getEndTime()),
//                        Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        }
//        return false;
//    }
//}
