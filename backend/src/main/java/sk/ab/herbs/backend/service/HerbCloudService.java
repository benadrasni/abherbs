package sk.ab.herbs.backend.service;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import sk.ab.herbs.backend.entity.Plant;

/**
 * Created by adrian on 1.9.2015.
 */
public interface HerbCloudService {

    @GET("_ah/api/taxonomyApi/v1/plant/{taxonName}/{taxonWiki}")
    Call<Plant> synchronizePlant(@Path("taxonName") String taxonName, @Path("taxonWiki") String taxonWiki);
}
