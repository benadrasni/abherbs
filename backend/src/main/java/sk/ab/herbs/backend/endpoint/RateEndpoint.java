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


import sk.ab.common.entity.Rate;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "rateApi",
        version = "v1",
        resource = "rate",
        namespace = @ApiNamespace(
                ownerDomain = "entity.backend.herbs.ab.sk",
                ownerName = "entity.backend.herbs.ab.sk",
                packagePath = ""
        )
)
public class RateEndpoint {

    private static final Logger logger = Logger.getLogger(RateEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Rate.class);
    }

    /**
     * Returns the {@link Rate} with the corresponding ID.
     *
     * @param rateId the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@link Rate} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "rate/{rateId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Rate get(@Named("rateId") Long rateId) throws NotFoundException {
        logger.info("Getting Rate with ID: " + rateId);
        Rate rate = ofy().load().type(Rate.class).id(rateId).now();
        if (rate == null) {
            throw new NotFoundException("Could not find Rate with ID: " + rateId);
        }
        return rate;
    }

    /**
     * Inserts a new {@link Rate}.
     */
    @ApiMethod(
            name = "insert",
            path = "rate",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Rate insert(Rate rate) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that rate.rateId has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(rate).now();
        logger.info("Created Rate with ID: " + rate.getId());

        return ofy().load().entity(rate).now();
    }

    /**
     * Updates an existing {@link Rate}.
     *
     * @param rateId the ID of the entity to be updated
     * @param rate   the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code rateId} does not correspond to an existing
     *                           {@link Rate}
     */
    @ApiMethod(
            name = "update",
            path = "rate/{rateId}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Rate update(@Named("rateId") Long rateId, Rate rate) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(rateId);
        ofy().save().entity(rate).now();
        logger.info("Updated Rate: " + rate);
        return ofy().load().entity(rate).now();
    }

    /**
     * Deletes the specified {@link Rate}.
     *
     * @param rateId the ID of the entity to delete
     * @throws NotFoundException if the {@code rateId} does not correspond to an existing
     *                           {@link Rate}
     */
    @ApiMethod(
            name = "remove",
            path = "rate/{rateId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("rateId") Long rateId) throws NotFoundException {
        checkExists(rateId);
        ofy().delete().type(Rate.class).id(rateId).now();
        logger.info("Deleted Rate with ID: " + rateId);
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
            path = "rate",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Rate> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Rate> query = ofy().load().type(Rate.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Rate> queryIterator = query.iterator();
        List<Rate> rateList = new ArrayList<Rate>(limit);
        while (queryIterator.hasNext()) {
            rateList.add(queryIterator.next());
        }
        return CollectionResponse.<Rate>builder().setItems(rateList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long rateId) throws NotFoundException {
        try {
            ofy().load().type(Rate.class).id(rateId).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Rate with ID: " + rateId);
        }
    }
}