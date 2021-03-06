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
public class BaseImageButton extends AppCompatButton {
    private long mLastClickTime;
    private String value;
    private boolean addDefaultListener;

    public BaseImageButton(Context context) {
        super(context);
        addDefaultListener = true;
        addListenerOnButton();
    }

    public BaseImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        addDefaultListener = true;
        setCustomAttributes(context, attrs);
        addListenerOnButton();
    }

    public BaseImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addDefaultListener = true;
        setCustomAttributes(context, attrs);
        addListenerOnButton();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setAddDefaultListener(boolean addDefaultListener) {
        this.addDefaultListener = addDefaultListener;
    }

    private void setCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BaseImageButton,0,0);

        try {
            this.value = attributes.getString(R.styleable.BaseImageButton_value);
            this.addDefaultListener = attributes.getBoolean(R.styleable.BaseImageButton_addDefaultListener, true);
        } finally {
            attributes.recycle();
        }
    }

    private void addListenerOnButton() {
        if (addDefaultListener) {
            setClickable(true);
            setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;
                    if (elapsedTime > AndroidConstants.MIN_CLICK_INTERVAL) {
                        if (((LinearLayout) view.getParent()).getContext() instanceof FilterPlantsBaseActivity) {
                            FilterPlantsBaseActivity host = (FilterPlantsBaseActivity) ((LinearLayout) view.getParent()).getContext();

                            if (host.getCounter() > 0 || host.getFilter().get(host.getCurrentFragment().getAttribute()) != null) {
                                host.addToFilter(value);
                            }
                        }
                    }
                }

            });
        }
    }
}
