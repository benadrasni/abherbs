package sk.ab.common.service;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import sk.ab.common.entity.Count;
import sk.ab.common.entity.Plant;
import sk.ab.common.entity.PlantList;
import sk.ab.common.entity.Rate;
import sk.ab.common.entity.Taxonomy;
import sk.ab.common.entity.Translation;
import sk.ab.common.entity.request.ListRequest;

/**
 * Datastore service
 */
public interface FirebaseService {

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("plants/{taxonomyName}.json")
    Call<Plant> savePlant(@Path("taxonomyName") String taxonomyName,
                                 @Body Plant plant);
}
