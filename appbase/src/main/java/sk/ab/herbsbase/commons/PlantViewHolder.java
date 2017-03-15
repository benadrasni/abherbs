package sk.ab.herbsbase.commons;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sk.ab.herbsbase.R;

/**
 *
 * Created by adrian on 14. 3. 2017.
 */

public class PlantViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView family;
    private ImageView familyIcon;
    private ImageView photo;

    public PlantViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.plant_title);
        family = (TextView) itemView.findViewById(R.id.plant_family);
        familyIcon = (ImageView) itemView.findViewById(R.id.family_icon);
        photo = (ImageView) itemView.findViewById(R.id.plant_photo);
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public TextView getFamily() {
        return family;
    }

    public void setFamily(TextView family) {
        this.family = family;
    }

    public ImageView getFamilyIcon() {
        return familyIcon;
    }

    public void setFamilyIcon(ImageView familyIcon) {
        this.familyIcon = familyIcon;
    }

    public ImageView getPhoto() {
        return photo;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }
}
