package sk.ab.herbsbase.commons;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import sk.ab.herbsbase.R;
import sk.ab.herbsbase.activities.FilterPlantsBaseActivity;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11.2.2013
 * Time: 18:54
 */
public class BaseImageButton extends AppCompatButton {
    private String value;

    public BaseImageButton(Context context) {
        super(context);
        addListenerOnButton();
    }

    public BaseImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomAttributes(context, attrs);
        addListenerOnButton();
    }

    public BaseImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomAttributes(context, attrs);
        addListenerOnButton();
    }

    public String getValue() {
        return value;
    }

    private void setCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BaseImageButton);
        for (int i = 0; i < attributes.getIndexCount(); ++i) {
            if (attributes.getIndex(i) == R.styleable.BaseImageButton_value) {
                this.value = attributes.getString(i);
                break;
            }
        }
        attributes.recycle();
    }

    private void addListenerOnButton() {
        setClickable(true);
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (((LinearLayout)view.getParent()).getContext() instanceof FilterPlantsBaseActivity) {
                    FilterPlantsBaseActivity host = (FilterPlantsBaseActivity) ((LinearLayout)view.getParent()).getContext();

                    if (host.getCounter() > 0 || host.getFilter().get(host.getCurrentFragment().getAttribute()) != null) {
                        host.addToFilter(value);
                    }
                }
            }

        });
    }
}
