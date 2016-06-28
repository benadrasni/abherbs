package sk.ab.herbs.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

import sk.ab.common.entity.PlantHeader;

/**
 * Wrapper for {@link PlantHeader}
 *
 */
public class PlantHeaderParcel extends PlantHeader implements Parcelable {

    public static final Creator<PlantHeaderParcel> CREATOR = new Creator<PlantHeaderParcel>() {
        @Override
        public PlantHeaderParcel createFromParcel(Parcel in) {
            return new PlantHeaderParcel(in);
        }

        @Override
        public PlantHeaderParcel[] newArray(int size) {
            return new PlantHeaderParcel[size];
        }
    };

    public PlantHeaderParcel(PlantHeader plantHeader) {
        id = plantHeader.getId();
        label = plantHeader.getLabel();
        url = plantHeader.getUrl();
        family = plantHeader.getFamily();
    }

    public PlantHeaderParcel(Parcel in) {
        id = in.readString();
        url = in.readString();
        label = (HashMap<String, String>) in.readSerializable();
        family = (HashMap<String, String>) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(url);
        parcel.writeSerializable(label);
        parcel.writeSerializable(family);
    }
}
