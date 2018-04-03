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
        setPlantId(plant.getPlantId());
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

        setFilterColor(plant.getFilterColor());
        setFilterHabitat(plant.getFilterHabitat());
        setFilterPetal(plant.getFilterPetal());
        setFilterInflorescence(plant.getFilterInflorescence());
        setFilterSepal(plant.getFilterSepal());
        setFilterStem(plant.getFilterStem());
        setFilterLeafShape(plant.getFilterLeafShape());
        setFilterLeafMargin(plant.getFilterLeafMargin());
        setFilterLeafVenetation(plant.getFilterLeafVenetation());
        setFilterLeafArrangement(plant.getFilterLeafArrangement());
        setFilterRoot(plant.getFilterRoot());
    }

    public PlantParcel(Parcel in) {
        setPlantId((Integer)in.readSerializable());
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

        setFilterColor((ArrayList<String>)in.readSerializable());
        setFilterHabitat((ArrayList<String>)in.readSerializable());
        setFilterPetal((ArrayList<String>)in.readSerializable());
        setFilterInflorescence((ArrayList<String>)in.readSerializable());
        setFilterSepal((ArrayList<String>)in.readSerializable());
        setFilterStem((ArrayList<String>)in.readSerializable());
        setFilterLeafShape((ArrayList<String>)in.readSerializable());
        setFilterLeafMargin((ArrayList<String>)in.readSerializable());
        setFilterLeafVenetation((ArrayList<String>)in.readSerializable());
        setFilterLeafArrangement((ArrayList<String>)in.readSerializable());
        setFilterRoot((ArrayList<String>)in.readSerializable());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(getPlantId());
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

        parcel.writeSerializable(getFilterColor());
        parcel.writeSerializable(getFilterHabitat());
        parcel.writeSerializable(getFilterPetal());
        parcel.writeSerializable(getFilterInflorescence());
        parcel.writeSerializable(getFilterSepal());
        parcel.writeSerializable(getFilterStem());
        parcel.writeSerializable(getFilterLeafShape());
        parcel.writeSerializable(getFilterLeafMargin());
        parcel.writeSerializable(getFilterLeafVenetation());
        parcel.writeSerializable(getFilterLeafArrangement());
        parcel.writeSerializable(getFilterRoot());
    }
}
