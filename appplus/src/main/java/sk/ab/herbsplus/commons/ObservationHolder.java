package sk.ab.herbsplus.commons;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sk.ab.herbsplus.R;

/**
 *
 * Created by adrian on 25. 1. 2018.
 */

public class ObservationHolder extends RecyclerView.ViewHolder {
    private TextView observationDate;
    private ImageView photo;

    public ObservationHolder(View itemView) {
        super(itemView);
        observationDate = itemView.findViewById(R.id.observation_date);
        photo = itemView.findViewById(R.id.observation_photo);
    }

    public TextView getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(TextView observationDate) {
        this.observationDate = observationDate;
    }

    public ImageView getPhoto() {
        return photo;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }
}
