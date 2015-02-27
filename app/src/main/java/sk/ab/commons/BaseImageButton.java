package sk.ab.commons;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import sk.ab.herbs.HerbsApp;
import sk.ab.herbs.R;
import sk.ab.herbs.activities.FilterPlantsActivity;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 11.2.2013
 * Time: 18:54
 */
public class BaseImageButton extends Button {
    private Integer valueId;

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

    public Integer getValueId() {
        return valueId;
    }

    private void setCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.BaseImageButton);
        for (int i = 0; i < attributes.getIndexCount(); ++i) {
            if (attributes.getIndex(i) == R.styleable.BaseImageButton_valueId) {
                this.valueId = attributes.getInt(i, 0);
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
                if (view.getContext() instanceof FilterPlantsActivity) {
                    FilterPlantsActivity host = (FilterPlantsActivity) view.getContext();
                    HerbsApp app = (HerbsApp)host.getApplication();

                    if (app.getCount() > 0 || app.getFilter().get(host.getCurrentFragment().getAttributeId()) != null) {
                        host.addToFilter(valueId);
                    }
                }
            }

        });
    }
}
