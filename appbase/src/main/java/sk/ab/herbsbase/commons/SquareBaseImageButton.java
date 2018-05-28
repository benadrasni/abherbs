package sk.ab.herbsbase.commons;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import sk.ab.herbsbase.AndroidConstants;
import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11.2.2013
 * Time: 18:54
 */
public class SquareBaseImageButton extends BaseImageButton {

    public SquareBaseImageButton(Context context) {
        super(context);
    }

    public SquareBaseImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareBaseImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
