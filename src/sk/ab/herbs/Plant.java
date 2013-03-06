package sk.ab.herbs;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 25.2.2013
 * Time: 19:14
 * To change this template use File | Settings | File Templates.
 */
public class Plant {
  private int plantId;
  private String title;
  private String family;

  private String flower_color;
  private String number_of_petals;
  private String sepal;
  private String inflorescence;
  private String leaf_shape;
  private String leaf_margin;
  private String leaf_venation;
  private String leaf_arrangement;
  private String stem;
  private String root;
  private String habitat;

  private ArrayList<String> photo_urls;

  public Plant(PlantHeader plantHeader) {
    this.plantId = plantHeader.getPlantId();
    this.title = plantHeader.getTitle();
    this.family = plantHeader.getFamily();
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

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public String getFlower_color() {
    return flower_color;
  }

  public void setFlower_color(String flower_color) {
    this.flower_color = flower_color;
  }

  public String getNumber_of_petals() {
    return number_of_petals;
  }

  public void setNumber_of_petals(String number_of_petals) {
    this.number_of_petals = number_of_petals;
  }

  public String getSepal() {
    return sepal;
  }

  public void setSepal(String sepal) {
    this.sepal = sepal;
  }

  public String getInflorescence() {
    return inflorescence;
  }

  public void setInflorescence(String inflorescence) {
    this.inflorescence = inflorescence;
  }

  public String getLeaf_shape() {
    return leaf_shape;
  }

  public void setLeaf_shape(String leaf_shape) {
    this.leaf_shape = leaf_shape;
  }

  public String getLeaf_margin() {
    return leaf_margin;
  }

  public void setLeaf_margin(String leaf_margin) {
    this.leaf_margin = leaf_margin;
  }

  public String getLeaf_venation() {
    return leaf_venation;
  }

  public void setLeaf_venation(String leaf_venation) {
    this.leaf_venation = leaf_venation;
  }

  public String getLeaf_arrangement() {
    return leaf_arrangement;
  }

  public void setLeaf_arrangement(String leaf_arrangement) {
    this.leaf_arrangement = leaf_arrangement;
  }

  public String getStem() {
    return stem;
  }

  public void setStem(String stem) {
    this.stem = stem;
  }

  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  public String getHabitat() {
    return habitat;
  }

  public void setHabitat(String habitat) {
    this.habitat = habitat;
  }
}
