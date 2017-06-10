package sk.ab.herbsbase.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.Plant;

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
        plantId = plant.getPlantId();
        name = plant.getName();
        wikiName = plant.getWikiName();
        illustrationUrl = plant.getIllustrationUrl();
        heightFrom = plant.getHeightFrom();
        heightTo = plant.getHeightTo();
        floweringFrom = plant.getFloweringFrom();
        floweringTo = plant.getFloweringTo();
        toxicityClass = plant.getToxicityClass();
        synonyms = plant.getSynonyms();
        photoUrls = plant.getPhotoUrls();
        taxonomy = plant.getTaxonomy();
        APGIV = plant.getAPGIV();
        wikilinks = plant.getWikilinks();

        filterColor = plant.getFilterColor();
        filterHabitat = plant.getFilterHabitat();
        filterPetal = plant.getFilterPetal();
        filterInflorescence = plant.getFilterInflorescence();
        filterSepal = plant.getFilterSepal();
        filterStem = plant.getFilterStem();
        filterLeafShape = plant.getFilterLeafShape();
        filterLeafMargin = plant.getFilterLeafMargin();
        filterLeafVenetation = plant.getFilterLeafVenetation();
        filterLeafArrangement = plant.getFilterLeafArrangement();
        filterRoot = plant.getFilterRoot();
    }

    public PlantParcel(Parcel in) {
        plantId = (Integer)in.readSerializable();
        name = in.readString();
        wikiName = in.readString();
        illustrationUrl = in.readString();
        heightFrom = (Integer)in.readSerializable();
        heightTo = (Integer)in.readSerializable();
        floweringFrom = (Integer)in.readSerializable();
        floweringTo = (Integer)in.readSerializable();
        toxicityClass = (Integer)in.readSerializable();
        synonyms = (ArrayList<String>)in.readSerializable();
        photoUrls = (ArrayList<String>)in.readSerializable();
        taxonomy = (HashMap<String, String>)in.readSerializable();
        APGIV = (HashMap<String, String>)in.readSerializable();
        wikilinks = (HashMap<String, String>)in.readSerializable();

        filterColor = (ArrayList<String>)in.readSerializable();
        filterHabitat = (ArrayList<String>)in.readSerializable();
        filterPetal = (ArrayList<String>)in.readSerializable();
        filterInflorescence = (ArrayList<String>)in.readSerializable();
        filterSepal = (ArrayList<String>)in.readSerializable();
        filterStem = (ArrayList<String>)in.readSerializable();
        filterLeafShape = (ArrayList<String>)in.readSerializable();
        filterLeafMargin = (ArrayList<String>)in.readSerializable();
        filterLeafVenetation = (ArrayList<String>)in.readSerializable();
        filterLeafArrangement = (ArrayList<String>)in.readSerializable();
        filterRoot = (ArrayList<String>)in.readSerializable();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeSerializable(plantId);
        parcel.writeString(name);
        parcel.writeString(wikiName);
        parcel.writeString(illustrationUrl);
        parcel.writeSerializable(heightFrom);
        parcel.writeSerializable(heightTo);
        parcel.writeSerializable(floweringFrom);
        parcel.writeSerializable(floweringTo);
        parcel.writeSerializable(toxicityClass);
        parcel.writeSerializable(synonyms);
        parcel.writeSerializable(photoUrls);
        parcel.writeSerializable(taxonomy);
        parcel.writeSerializable(APGIV);
        parcel.writeSerializable(wikilinks);

        parcel.writeSerializable(filterColor);
        parcel.writeSerializable(filterHabitat);
        parcel.writeSerializable(filterPetal);
        parcel.writeSerializable(filterInflorescence);
        parcel.writeSerializable(filterSepal);
        parcel.writeSerializable(filterStem);
        parcel.writeSerializable(filterLeafShape);
        parcel.writeSerializable(filterLeafMargin);
        parcel.writeSerializable(filterLeafVenetation);
        parcel.writeSerializable(filterLeafArrangement);
        parcel.writeSerializable(filterRoot);
    }
}
