package sk.ab.herbsbase.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import sk.ab.common.entity.PlantTranslation;

/**
 *
 * Created by adrian on 8. 4. 2017.
 */

public class PlantTranslationParcel extends PlantTranslation implements Parcelable {

    public PlantTranslationParcel(PlantTranslation plantTranslation) {
        if (plantTranslation != null) {
            setLabel(plantTranslation.getLabel());
            setNames(plantTranslation.getNames());
            setSourceUrls(plantTranslation.getSourceUrls());
            setDescription(plantTranslation.getDescription());
            setFlower(plantTranslation.getFlower());
            setInflorescence(plantTranslation.getInflorescence());
            setFruit(plantTranslation.getFruit());
            setLeaf(plantTranslation.getLeaf());
            setStem(plantTranslation.getStem());
            setHabitat(plantTranslation.getHabitat());
            setTrivia(plantTranslation.getTrivia());
            setToxicity(plantTranslation.getToxicity());
            setHerbalism(plantTranslation.getHerbalism());
        }
    }

    protected PlantTranslationParcel(Parcel in) {
        setLabel(in.readString());
        setNames((ArrayList<String>)in.readSerializable());
        setSourceUrls((ArrayList<String>)in.readSerializable());
        setDescription(in.readString());
        setFlower(in.readString());
        setInflorescence(in.readString());
        setFruit(in.readString());
        setLeaf(in.readString());
        setStem(in.readString());
        setHabitat(in.readString());
        setTrivia(in.readString());
        setToxicity(in.readString());
        setHerbalism(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getLabel());
        dest.writeSerializable(getNames());
        dest.writeSerializable(getSourceUrls());
        dest.writeString(getDescription());
        dest.writeString(getFlower());
        dest.writeString(getInflorescence());
        dest.writeString(getFruit());
        dest.writeString(getLeaf());
        dest.writeString(getStem());
        dest.writeString(getHabitat());
        dest.writeString(getTrivia());
        dest.writeString(getToxicity());
        dest.writeString(getHerbalism());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlantTranslationParcel> CREATOR = new Creator<PlantTranslationParcel>() {
        @Override
        public PlantTranslationParcel createFromParcel(Parcel in) {
            return new PlantTranslationParcel(in);
        }

        @Override
        public PlantTranslationParcel[] newArray(int size) {
            return new PlantTranslationParcel[size];
        }
    };
}
