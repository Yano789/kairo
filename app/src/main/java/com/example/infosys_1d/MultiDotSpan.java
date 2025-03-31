package com.example.infosys_1d;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import java.util.List;

/**
 * MultiDotSpan is a custom span that adds multiple colored dots under a calendar day
 */
public class MultiDotSpan implements LineBackgroundSpan{
    private final int radius;
    private final List<Integer> colors;

    public MultiDotSpan(int radius, List<Integer> colors){
        this.radius = radius;
        this.colors = colors;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int lineNumber){
        if (colors == null || colors.isEmpty()){
            return;
        }
        int totalDots = Math.min(colors.size(), 3);
        int spacing = 2*radius + 4;
        int totalWidth = (totalDots - 1) * spacing;

        int centerX = (left + right) / 2;
        int startX = centerX - totalWidth / 2;
        int y = bottom + radius + 2;

        for (int i = 0; i < totalDots; i++) {
            paint.setColor(colors.get(i));
            canvas.drawCircle(startX + i * spacing, y, radius, paint);
        }
    }
}