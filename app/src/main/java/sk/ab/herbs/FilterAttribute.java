package sk.ab.herbs;

/**
 * Created by adrian on 1.9.2015.
 */
public class FilterAttribute {
    private Integer attributeId;
    private Integer valueId;

    public FilterAttribute(Integer attributeId, Integer valueId) {
        this.attributeId = attributeId;
        this.valueId = valueId;
    }
}
