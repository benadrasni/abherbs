package sk.ab.herbs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 25.2.2013
 * Time: 19:14
 * To change this template use File | Settings | File Templates.
 */
public class PlantHeader implements Parcelable {

  public static final Parcelable.Creator<PlantHeader> CREATOR = new Parcelable.Creator<PlantHeader>() {
    public PlantHeader createFromParcel(Parcel in) {
      return new PlantHeader(in);
    }

    public PlantHeader[] newArray (int size) {
      return new PlantHeader[size];
    }
  };

  private int plantId;
  private String title;
  private int iconRes;
  private String url;
  private String family;
  private int familyId;

  public PlantHeader(int plantId, String title) {
    this.plantId = plantId;
    this.title = title;
    this.iconRes = R.drawable.home;
  }

  public PlantHeader(int plantId, String title, String url) {
    this(plantId, title);
    this.url = url;
  }

  public PlantHeader(int plantId, String title, String url, String family, int familyId) {
    this(plantId, title, url);
    this.family = family;
    this.familyId = familyId;
  }

  public PlantHeader (Parcel in) {
    plantId = in.readInt();
    title = in.readString();
    url = in.readString();
    familyId = in.readInt();
    family = in.readString();
  }

  @Override
  public int describeContents () {
    return 0;
  }

  @Override
  public void writeToParcel (Parcel destination, int flags) {
    destination.writeInt(plantId);
    destination.writeString(title);
    destination.writeString(url);
    destination.writeInt(familyId);
    destination.writeString(family);
  }

  public int getPlantId() {
    return plantId;
  }

  public void setPlantId(int plantId) {
    this.plantId = plantId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getIconRes() {
    return iconRes;
  }

  public void setIconRes(int iconRes) {
    this.iconRes = iconRes;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public int getFamilyId() {
    return familyId;
  }

  public void setFamilyId(int familyId) {
    this.familyId = familyId;
  }
}
