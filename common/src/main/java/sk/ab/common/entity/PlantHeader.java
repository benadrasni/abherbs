package sk.ab.common.entity;

/**
 *
 */
public class PlantHeader {

  protected String id;
  protected String label;
  protected String url;
  protected String familyLatin;
  protected String family;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getFamilyLatin() {
    return familyLatin;
  }

  public void setFamilyLatin(String familyLatin) {
    this.familyLatin = familyLatin;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }
}
