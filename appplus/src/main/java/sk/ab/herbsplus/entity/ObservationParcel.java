package sk.ab.herbsplus.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

import sk.ab.common.entity.Observation;

/**
 *
 * Created by adrian on 28. 1. 2018.
 */

public class ObservationParcel extends Observation implements Parcelable {

    public static final Creator<ObservationParcel> CREATOR = new Creator<ObservationParcel>() {
        @Override
        public ObservationParcel createFromParcel(Parcel in) {
            return new ObservationParcel(in);
        }

        @Override
        public ObservationParcel[] newArray(int size) {
            return new ObservationParcel[size];
        }
    };

    public ObservationParcel(Observation observation) {
        setId(observation.getId());
        setPlant(observation.getPlant());
        setDate(observation.getDate());
        setLatitude(observation.getLatitude());
        setLongitude(observation.getLongitude());
        setNote(observation.getNote());
        setStatus(observation.getStatus());
        setPhotoPaths(observation.getPhotoPaths());
    }

    public ObservationParcel(Parcel in) {
        setId(in.readString());
        setPlant(in.readString());
        setDate((Date)in.readSerializable());
        setLatitude((Double)in.readSerializable());
        setLongitude((Double)in.readSerializable());
        setNote(in.readString());
        setStatus(in.readString());
        setPhotoPaths((ArrayList<String>)in.readSerializable());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getId());
        parcel.writeString(getPlant());
        parcel.writeSerializable(getDate());
        parcel.writeSerializable(getLatitude());
        parcel.writeSerializable(getLongitude());
        parcel.writeString(getNote());
        parcel.writeString(getStatus());
        parcel.writeSerializable(getPhotoPaths());
    }
}
