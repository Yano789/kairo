package com.example.infosys_1d.Calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;
import java.util.List;

public class MultiDotSpan implements LineBackgroundSpan {
    private static final int MAX_DOTS = 3; // Max dots to show
    private static final int DEFAULT_RADIUS = 4; // Dot radius (dp)
    private static final int DOT_SPACING = 8; // Space between dots (dp)
    private static final int DOT_Y_OFFSET = 10; // How far below the date to place dots (dp)

    private final int radius;
    private final List<Integer> colors;

    public MultiDotSpan(int radius, List<Integer> colors) {
        this.radius = radius;
        this.colors = colors;
    }

    @Override
    public void drawBackground(
            Canvas canvas, Paint paint,
            int left, int right, int top, int baseline, int bottom,
            CharSequence text, int start, int end, int lineNumber
    ) {
        if (colors == null || colors.isEmpty()) {
            return;
        }

        int totalDots = Math.min(colors.size(), MAX_DOTS);
        int totalWidth = (totalDots - 1) * DOT_SPACING;
        int startX = (left + right) / 2 - totalWidth / 2;
        int centerY = bottom + DOT_Y_OFFSET;

        for (int i = 0; i < totalDots; i++) {
            paint.setColor(colors.get(i));
            canvas.drawCircle(startX + i * DOT_SPACING, centerY, radius, paint);
        }
    }
}