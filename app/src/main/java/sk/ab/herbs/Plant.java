package sk.ab.herbs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import sk.ab.tools.TextWithLanguage;

/**
 * Created with IntelliJ IDEA.
 * User: adrian
 * Date: 25.2.2013
 * Time: 19:14
 *
 */
public class Plant implements Parcelable {

    public static final Parcelable.Creator<Plant> CREATOR = new Parcelable.Creator<Plant>() {
        public Plant createFromParcel(Parcel in) {
            return new Plant(in);
        }

        public Plant[] newArray(int size) {
            return new Plant[size];
        }
    };

    private int plantId;
    private String title;
    private String back_url;
    private int toxicity_class;
    private int height_from;
    private int height_to;
    private int flowering_from;
    private int flowering_to;
    private TextWithLanguage description;
    private TextWithLanguage flower;
    private TextWithLanguage inflorescence;
    private TextWithLanguage fruit;
    private TextWithLanguage leaf;
    private TextWithLanguage stem;
    private TextWithLanguage habitat;
    private TextWithLanguage trivia;
    private TextWithLanguage toxicity;
    private TextWithLanguage herbalism;
    private String root;
    private String flower_color;
    private String number_of_petals;
    private String sepal;
    private String leaf_shape;
    private String leaf_margin;
    private String leaf_venation;
    private String leaf_arrangement;
    private List<String> names;
    private List<String> photo_urls;
    private List<String> source_urls;
    private String domain;
    private String domain_latin;
    private String kingdom;
    private String kingdom_latin;
    private String subkingdom;
    private String subkingdom_latin;
    private String line;
    private String line_latin;
    private String branch;
    private String branch_latin;
    private String phylum;
    private String phylum_latin;
    private String cls;
    private String cls_latin;
    private String order;
    private String order_latin;
    private String family;
    private String family_latin;
    private String genus;
    private String genus_latin;
    private String species;
    private String species_latin;

    public Plant(int plantId) {
        this.plantId = plantId;
    }

    public Plant(Parcel in) {
        plantId = in.readInt();
        title = in.readString();
        back_url = in.readString();
        toxicity_class = in.readInt();
        height_from = in.readInt();
        height_to = in.readInt();
        flowering_from = in.readInt();
        flowering_to = in.readInt();

        description = new TextWithLanguage();
        int size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            description.add(key,value);
        }

        flower = new TextWithLanguage();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            flower.add(key,value);
        }

        inflorescence = new TextWithLanguage();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            inflorescence.add(key,value);
        }

        fruit = new TextWithLanguage();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            fruit.add(key,value);
        }

        leaf = new TextWithLanguage();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            leaf.add(key,value);
        }

        stem = new TextWithLanguage();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            stem.add(key,value);
        }

        habitat = new TextWithLanguage();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            habitat.add(key,value);
        }

        trivia = new TextWithLanguage();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            trivia.add(key,value);
        }

        toxicity = new TextWithLanguage();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            toxicity.add(key,value);
        }

        herbalism = new TextWithLanguage();
        size = in.readInt();
        for(int i = 0; i < size; i++){
            Integer key = in.readInt();
            String value = in.readString();
            herbalism.add(key,value);
        }

        flower_color = in.readString();
        number_of_petals = in.readString();
        sepal = in.readString();
        leaf_shape = in.readString();
        leaf_margin = in.readString();
        leaf_venation = in.readString();
        leaf_arrangement = in.readString();
        root = in.readString();
        domain = in.readString();
        domain_latin = in.readString();
        kingdom = in.readString();
        kingdom_latin = in.readString();
        subkingdom = in.readString();
        subkingdom_latin = in.readString();
        line = in.readString();
        line_latin = in.readString();
        branch = in.readString();
        branch_latin = in.readString();
        phylum = in.readString();
        phylum_latin = in.readString();
        cls = in.readString();
        cls_latin = in.readString();
        order = in.readString();
        order_latin = in.readString();
        family = in.readString();
        family_latin = in.readString();
        genus = in.readString();
        genus_latin = in.readString();
        species = in.readString();
        species_latin = in.readString();

        names = new ArrayList<String>();
        int n = in.readInt();
        for(int i=0; i < n; i++) {
            names.add(in.readString());
        }

        photo_urls = new ArrayList<String>();
        n = in.readInt();
        for(int i=0; i < n; i++) {
            photo_urls.add(in.readString());
        }

        source_urls = new ArrayList<String>();
        n = in.readInt();
        for(int i=0; i < n; i++) {
            source_urls.add(in.readString());
        }
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel destination, int flags) {
        destination.writeInt(plantId);
        destination.writeString(title);
        destination.writeString(back_url);
        destination.writeInt(toxicity_class);
        destination.writeInt(height_from);
        destination.writeInt(height_to);
        destination.writeInt(flowering_from);
        destination.writeInt(flowering_to);
        description.writeToParcel(destination, flags);
        flower.writeToParcel(destination, flags);
        inflorescence.writeToParcel(destination, flags);
        fruit.writeToParcel(destination, flags);
        leaf.writeToParcel(destination, flags);
        stem.writeToParcel(destination, flags);
        habitat.writeToParcel(destination, flags);
        trivia.writeToParcel(destination, flags);
        toxicity.writeToParcel(destination, flags);
        herbalism.writeToParcel(destination, flags);
        destination.writeString(flower_color);
        destination.writeString(number_of_petals);
        destination.writeString(sepal);
        destination.writeString(leaf_shape);
        destination.writeString(leaf_margin);
        destination.writeString(leaf_venation);
        destination.writeString(leaf_arrangement);
        destination.writeString(root);
        destination.writeString(domain);
        destination.writeString(domain_latin);
        destination.writeString(kingdom);
        destination.writeString(kingdom_latin);
        destination.writeString(subkingdom);
        destination.writeString(subkingdom_latin);
        destination.writeString(line);
        destination.writeString(line_latin);
        destination.writeString(branch);
        destination.writeString(branch_latin);
        destination.writeString(phylum);
        destination.writeString(phylum_latin);
        destination.writeString(cls);
        destination.writeString(cls_latin);
        destination.writeString(order);
        destination.writeString(order_latin);
        destination.writeString(family);
        destination.writeString(family_latin);
        destination.writeString(genus);
        destination.writeString(genus_latin);
        destination.writeString(species);
        destination.writeString(species_latin);

        if (names != null) {
            destination.writeInt(names.size());
            for(String name : names) {
                destination.writeString(name);
            }
        } else {
            destination.writeInt(0);
        }

        if (photo_urls != null) {
            destination.writeInt(photo_urls.size());
            for(String url : photo_urls) {
                destination.writeString(url);
            }
        } else {
            destination.writeInt(0);
        }

        if (source_urls != null) {
            destination.writeInt(source_urls.size());
            for(String url : source_urls) {
                destination.writeString(url);
            }
        } else {
            destination.writeInt(0);
        }

    }

    public String getSpeciesShort() {
        if (species.indexOf(' ') > -1) {
            return species.substring(0, 1) + "" + species.substring(species.indexOf(' ') + 1);
        } else {
            return species;
        }
    }

    public String getSpecies_latinShort() {
        if (species_latin.indexOf(' ') > -1) {
            return species_latin.substring(0, 1) + "" + species_latin.substring(species_latin.indexOf(' ') + 1);
        } else {
            return species_latin;
        }
    }

    public String getDescWithHighlight(String label, String desc) {
        String text = "<b>" + label + ":</b> ";
        if (desc != null) {
            text = text + desc;
        }
        return text;
    }

    public boolean isTranslated(int language) {
        return description.isText(language) && flower.isText(language) && inflorescence.isText(language)
                && fruit.isText(language) && leaf.isText(language) && stem.isText(language) && habitat.isText(language);
    }

    // getters and setters

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

    public int getToxicity_class() {
        return toxicity_class;
    }

    public void setToxicity_class(int toxicity_class) {
        this.toxicity_class = toxicity_class;
    }

    public int getHeight_from() {
        return height_from;
    }

    public void setHeight_from(int height_from) {
        this.height_from = height_from;
    }

    public int getHeight_to() {
        return height_to;
    }

    public void setHeight_to(int height_to) {
        this.height_to = height_to;
    }

    public int getFlowering_from() {
        return flowering_from;
    }

    public void setFlowering_from(int flowering_from) {
        this.flowering_from = flowering_from;
    }

    public int getFlowering_to() {
        return flowering_to;
    }

    public void setFlowering_to(int flowering_to) {
        this.flowering_to = flowering_to;
    }

    public TextWithLanguage getDescription() {
        return description;
    }

    public void setDescription(TextWithLanguage description) {
        this.description = description;
    }

    public String getBack_url() {
        return back_url;
    }

    public void setBack_url(String back_url) {
        this.back_url = back_url;
    }

    public TextWithLanguage getFlower() {
        return flower;
    }

    public void setFlower(TextWithLanguage flower) {
        this.flower = flower;
    }

    public TextWithLanguage getFruit() {
        return fruit;
    }

    public void setFruit(TextWithLanguage fruit) {
        this.fruit = fruit;
    }

    public TextWithLanguage getLeaf() {
        return leaf;
    }

    public void setLeaf(TextWithLanguage leaf) {
        this.leaf = leaf;
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

    public TextWithLanguage getInflorescence() {
        return inflorescence;
    }

    public void setInflorescence(TextWithLanguage inflorescence) {
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

    public TextWithLanguage getStem() {
        return stem;
    }

    public void setStem(TextWithLanguage stem) {
        this.stem = stem;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public TextWithLanguage getHabitat() {
        return habitat;
    }

    public void setHabitat(TextWithLanguage habitat) {
        this.habitat = habitat;
    }

    public TextWithLanguage getTrivia() {
        return trivia;
    }

    public void setTrivia(TextWithLanguage trivia) {
        this.trivia = trivia;
    }

    public TextWithLanguage getToxicity() {
        return toxicity;
    }

    public void setToxicity(TextWithLanguage toxicity) {
        this.toxicity = toxicity;
    }

    public TextWithLanguage getHerbalism() {
        return herbalism;
    }

    public void setHerbalism(TextWithLanguage herbalism) {
        this.herbalism = herbalism;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain_latin() {
        return domain_latin;
    }

    public void setDomain_latin(String domain_latin) {
        this.domain_latin = domain_latin;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public String getKingdom_latin() {
        return kingdom_latin;
    }

    public void setKingdom_latin(String kingdom_latin) {
        this.kingdom_latin = kingdom_latin;
    }

    public String getSubkingdom() {
        return subkingdom;
    }

    public void setSubkingdom(String subkingdom) {
        this.subkingdom = subkingdom;
    }

    public String getSubkingdom_latin() {
        return subkingdom_latin;
    }

    public void setSubkingdom_latin(String subkingdom_latin) {
        this.subkingdom_latin = subkingdom_latin;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLine_latin() {
        return line_latin;
    }

    public void setLine_latin(String line_latin) {
        this.line_latin = line_latin;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getBranch_latin() {
        return branch_latin;
    }

    public void setBranch_latin(String branch_latin) {
        this.branch_latin = branch_latin;
    }

    public String getPhylum() {
        return phylum;
    }

    public void setPhylum(String phylum) {
        this.phylum = phylum;
    }

    public String getPhylum_latin() {
        return phylum_latin;
    }

    public void setPhylum_latin(String phylum_latin) {
        this.phylum_latin = phylum_latin;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public String getCls_latin() {
        return cls_latin;
    }

    public void setCls_latin(String cls_latin) {
        this.cls_latin = cls_latin;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrder_latin() {
        return order_latin;
    }

    public void setOrder_latin(String order_latin) {
        this.order_latin = order_latin;
    }

    public String getFamily_latin() {
        return family_latin;
    }

    public void setFamily_latin(String family_latin) {
        this.family_latin = family_latin;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getGenus_latin() {
        return genus_latin;
    }

    public void setGenus_latin(String genus_latin) {
        this.genus_latin = genus_latin;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSpecies_latin() {
        return species_latin;
    }

    public void setSpecies_latin(String species_latin) {
        this.species_latin = species_latin;
    }

    public List<String> getPhoto_urls() {
        return photo_urls;
    }

    public void setPhoto_urls(List<String> photo_urls) {
        this.photo_urls = photo_urls;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getSource_urls() {
        return source_urls;
    }

    public void setSource_urls(List<String> source_urls) {
        this.source_urls = source_urls;
    }
}
