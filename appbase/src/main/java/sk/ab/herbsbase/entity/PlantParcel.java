package sk.ab.herbsbase.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

import sk.ab.common.entity.FirebasePlant;

/**
 * Wrapper for {@link FirebasePlant}
 */
public class PlantParcel extends FirebasePlant implements Parcelable {

    public static final Creator<PlantParcel> CREATOR = new Creator<PlantParcel>() {
        @Override
        public PlantParcel createFromParcel(Parcel in) {
            return new PlantParcel(in);
        }

        @Override
        public PlantParcel[] newArray(int size) {
            return new PlantParcel[size];
        }
    };

    public PlantParcel(FirebasePlant plant) {
        setName(plant.getName());
        setWikiName(plant.getWikiName());
        setIllustrationUrl(plant.getIllustrationUrl());
        setHeightFrom(plant.getHeightFrom());
        setHeightTo(plant.getHeightTo());
        setFloweringFrom(plant.getFloweringFrom());
        setFloweringTo(plant.getFloweringTo());
        setToxicityClass(plant.getToxicityClass());
        setSynonyms(plant.getSynonyms());
        setPhotoUrls(plant.getPhotoUrls());
        setSourceUrls(plant.getSourceUrls());
        setTaxonomy(plant.getTaxonomy());
        setAPGIV(plant.getAPGIV());
        setWikilinks(plant.getWikilinks());
    }

    public PlantParcel(Parcel in) {
        setName(in.readString());
        setWikiName(in.readString());
        setIllustrationUrl(in.readString());
        setHeightFrom((Integer)in.readSerializable());
        setHeightTo((Integer)in.readSerializable());
        setFloweringFrom((Integer)in.readSerializable());
        setFloweringTo((Integer)in.readSerializable());
        setToxicityClass((Integer)in.readSerializable());
        setSynonyms((ArrayList<String>)in.readSerializable());
        setPhotoUrls((ArrayList<String>)in.readSerializable());
        setSourceUrls((ArrayList<String>)in.readSerializable());
        setTaxonomy((HashMap<String, String>)in.readSerializable());
        setAPGIV((HashMap<String, String>)in.readSerializable());
        setWikilinks((HashMap<String, String>)in.readSerializable());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getName());
        parcel.writeString(getWikiName());
        parcel.writeString(getIllustrationUrl());
        parcel.writeSerializable(getHeightFrom());
        parcel.writeSerializable(getHeightTo());
        parcel.writeSerializable(getFloweringFrom());
        parcel.writeSerializable(getFloweringTo());
        parcel.writeSerializable(getToxicityClass());
        parcel.writeSerializable(getSynonyms());
        parcel.writeSerializable(getPhotoUrls());
        parcel.writeSerializable(getSourceUrls());
        parcel.writeSerializable(getTaxonomy());
        parcel.writeSerializable(getAPGIV());
        parcel.writeSerializable(getWikilinks());
    }
}
