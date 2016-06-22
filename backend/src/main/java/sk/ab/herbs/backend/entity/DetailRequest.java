package sk.ab.herbs.backend.entity;

/**
 * Created by adrian on 1.9.2015.
 */
public class DetailRequest {
    private Integer langId;
    private Integer objectId;

    public DetailRequest(Integer langId, Integer objectId) {
        this.langId = langId;
        this.objectId = objectId;
    }
}
