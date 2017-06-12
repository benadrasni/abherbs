package sk.ab.common.service;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import sk.ab.common.entity.Count;
import sk.ab.common.entity.FirebasePlant;
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
    Call<FirebasePlant> savePlant(@Path("taxonomyName") String taxonomyName, @Body FirebasePlant plant);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("plants/{taxonomyName}/APGIV.json")
    Call<HashMap<String, String>> savePlantAPGIV(@Path("taxonomyName") String taxonomyName, @Body HashMap<String, String> apgiv);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("plants/{taxonomyName}/taxonomy.json")
    Call<HashMap<String, String>> savePlantTaxonomy(@Path("taxonomyName") String taxonomyName, @Body HashMap<String, String> apgiii);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("plants/{name}.json")
    Call<FirebasePlant> getPlant(@Path("name") String name);

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
    @PUT("search_new/{language}/{name}.json")
    Call<Map<String, Boolean>> saveName(@Path("language") String language, @Path("name") String name, @Body Map<String, Boolean> plants);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("search/{language}.json")
    Call<Object> saveSearch(@Path("language") String language, @Body Map<String, Map<String, Boolean>> plants);

    @PUT("APG III.json")
    Call<Object> saveAPGIII(@Body Object object);

    @PUT("APG III_new.json")
    Call<Object> saveAPGIIINew(@Body Object object);

    @GET("APG III_new.json")
    Call<Map<String, Object>> getAPGIIINew();

    @GET("APG IV.json")
    Call<Map<String, Object>> getAPGIV();

    @PUT("APG IV.json")
    Call<Object> saveAPGIV(@Body Object object);

    @PUT("translations.json")
    Call<Object> saveTranslations(@Body Object object);

    @PUT("translations/{language}/{name}.json")
    Call<Object> saveTranslation(@Path("language") String language, @Path("name") String name, @Body Object object);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("translations/{language}/{name}.json")
    Call<Map<String, Object>> getTranslation(@Path("language") String language, @Path("name") String name);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("translations.json")
    Call<Map<String, Object>> getTranslation();

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("translations/{language}.json")
    Call<Map<String, Object>> getTranslation(@Path("language") String language);
}
