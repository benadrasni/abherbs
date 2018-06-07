package sk.ab.herbsbase.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
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
        name = plantHeader.getName();
        family = plantHeader.getFamily();
        url = plantHeader.getUrl();
        filterColor = plantHeader.getFilterColor();
        filterDistribution = plantHeader.getFilterDistribution();
        filterHabitat = plantHeader.getFilterHabitat();
        filterPetal = plantHeader.getFilterPetal();
    }

    public PlantHeaderParcel(Parcel in) {
        name = in.readString();
        family = in.readString();
        url = in.readString();
        filterColor = (ArrayList<Integer>) in.readSerializable();
        filterDistribution = (ArrayList<Integer>) in.readSerializable();
        filterHabitat = (ArrayList<Integer>) in.readSerializable();
        filterPetal = (ArrayList<Integer>) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(family);
        parcel.writeString(url);
        parcel.writeSerializable(filterColor);
        parcel.writeSerializable(filterDistribution);
        parcel.writeSerializable(filterHabitat);
        parcel.writeSerializable(filterPetal);
    }
}
