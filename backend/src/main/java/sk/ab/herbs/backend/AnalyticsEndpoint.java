package sk.ab.herbs.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.ObjectifyService;

import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 *
 */
@Api(
        name = "analyticsApi",
        version = "v1",
        resource = "analytics",
        namespace = @ApiNamespace(
                ownerDomain = "backend.herbs.ab.sk",
                ownerName = "backend.herbs.ab.sk",
                packagePath = ""
        )
)
public class AnalyticsEndpoint {

    private static final Logger logger = Logger.getLogger(AnalyticsEndpoint.class.getName());

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Rate.class);
    }

    /**
     * Inserts a new {@code Rate}.
     */
    @ApiMethod(
            name = "insert",
            path = "analytics",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Rate insert(Rate rate) {
        ofy().save().entity(rate).now();
        logger.info("Created Rate with ID: " + rate.getDate());

        return ofy().load().entity(rate).now();
    }
}