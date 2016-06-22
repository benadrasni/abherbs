package sk.ab.herbs.backend.service;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import sk.ab.herbs.backend.entity.Plant;

/**
 * Created by adrian on 1.9.2015.
 */
public interface HerbCloudService {

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @POST("_ah/api/taxonomyApi/v1/plant/{taxonomyName}")
    Call<Plant> synchronizePlant(@Path("taxonomyName") String taxonomyName,
                                 @Query("taxonomyWiki") String taxonomyWiki,
                                 @Body Plant plant);
}
