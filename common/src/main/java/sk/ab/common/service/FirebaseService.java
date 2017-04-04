package sk.ab.common.service;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import sk.ab.common.entity.Count;
import sk.ab.common.entity.Plant;
import sk.ab.common.entity.PlantList;


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
    @PATCH("counts.json")
    Call<Map> saveCount(@Body Map<String, Integer> count);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PATCH("lists.json")
    Call<Map> saveList(@Body Map<String, Map<String, Boolean>> list);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("search/{language}/{name}.json")
    Call<Map<String, Boolean>> saveName(@Path("language") String language, @Path("name") String name, @Body Map<String, Boolean> plants);

    @PUT("APG III.json")
    Call<Object> saveAPGIII(@Body Object object);
}
