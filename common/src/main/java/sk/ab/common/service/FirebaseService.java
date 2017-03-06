package sk.ab.common.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
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
    @GET("filters/{filter}/count.json")
    Call<Count> getCount(@Path("filter") String filter);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("filters/{filter}/list.json")
    Call<PlantList> getList(@Path("filter") String filter);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("plants/{name}.json")
    Call<Plant> getDetail(@Path("name") String name);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("plants/{taxonomyName}.json")
    Call<Plant> savePlant(@Path("taxonomyName") String taxonomyName, @Body Plant plant);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("filters/{filter}/count.json")
    Call<Count> saveCount(@Path("filter") String filter, @Body Count count);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("filters/{filter}/list.json")
    Call<PlantList> saveList(@Path("filter") String filter, @Body PlantList list);
}
