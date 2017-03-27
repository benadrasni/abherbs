package sk.ab.herbsplus.commons;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import sk.ab.herbsplus.R;

/**
 *
 * Created by adrian on 26. 3. 2017.
 */

public class NameViewHolder extends RecyclerView.ViewHolder {
    private TextView name;

    public NameViewHolder(View itemView) {
        super(itemView);
        name= (TextView) itemView.findViewById(R.id.name);
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }
}
