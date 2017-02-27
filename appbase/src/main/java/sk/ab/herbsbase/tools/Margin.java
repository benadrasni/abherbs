package sk.ab.herbsbase.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * Created by adrian on 13.12.2014.
 * <p/>
 */
public class Margin implements LeadingMarginSpan.LeadingMarginSpan2 {
    private int margin;
    private int lines;

    public Margin(int lines, int margin) {
        this.margin = margin;
        this.lines = lines;
    }

    @Override
    public int getLeadingMarginLineCount() {
        return lines;
    }

    @Override
    public int getLeadingMargin(boolean first) {
        if (first) {
            return margin;
        } else {
            return 0;
        }
    }

    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int i, int i2, int i3, int i4, int i5,
                                  CharSequence charSequence, int i6, int i7, boolean b, Layout layout) {

    }
}