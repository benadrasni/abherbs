package sk.ab.herbs.backend.endpoint;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import sk.ab.herbs.backend.entity.Translation;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "translationApi",
        version = "v1",
        resource = "translation",
        namespace = @ApiNamespace(
                ownerDomain = "backend.herbs.ab.sk",
                ownerName = "backend.herbs.ab.sk",
                packagePath = "endpoint"
        )
)
public class TranslationEndpoint {

    private static final Logger logger = Logger.getLogger(TranslationEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Translation.class);
    }

    /**
     * Returns the {@link Translation} with the corresponding ID.
     *
     * @param translationId the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Translation} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "translation/{translationId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Translation get(@Named("translationId") String translationId) throws NotFoundException {
        logger.info("Getting Translation with ID: " + translationId);
        Translation translation = ofy().load().type(Translation.class).id(translationId).now();
        if (translation == null) {
            throw new NotFoundException("Could not find Translation with ID: " + translationId);
        }
        return translation;
    }

    /**
     * Inserts a new {@code Translation}.
     */
    @ApiMethod(
            name = "insert",
            path = "translation",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Translation insert(Translation translation) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that translation.translationId has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(translation).now();
        logger.info("Created Translation with ID: " + translation.getTransId());

        return ofy().load().entity(translation).now();
    }

    /**
     * Updates an existing {@code Translation}.
     *
     * @param translationId the ID of the entity to be updated
     * @param translation   the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code translationId} does not correspond to an existing
     *                           {@code Translation}
     */
    @ApiMethod(
            name = "update",
            path = "translation/{translationId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Translation update(@Named("translationId") String translationId, Translation translation) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(translationId);
        ofy().save().entity(translation).now();
        logger.info("Updated Translation: " + translation);
        return ofy().load().entity(translation).now();
    }

    /**
     * Deletes the specified {@code Translation}.
     *
     * @param translationId the ID of the entity to delete
     * @throws NotFoundException if the {@code translationId} does not correspond to an existing
     *                           {@code Translation}
     */
    @ApiMethod(
            name = "remove",
            path = "translation/{translationId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("translationId") String translationId) throws NotFoundException {
        checkExists(translationId);
        ofy().delete().type(Translation.class).id(translationId).now();
        logger.info("Deleted Translation with ID: " + translationId);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "translation",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Translation> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Translation> query = ofy().load().type(Translation.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Translation> queryIterator = query.iterator();
        List<Translation> translationList = new ArrayList<Translation>(limit);
        while (queryIterator.hasNext()) {
            translationList.add(queryIterator.next());
        }
        return CollectionResponse.<Translation>builder().setItems(translationList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(String translationId) throws NotFoundException {
        try {
            ofy().load().type(Translation.class).id(translationId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Translation with ID: " + translationId);
        }
    }
}