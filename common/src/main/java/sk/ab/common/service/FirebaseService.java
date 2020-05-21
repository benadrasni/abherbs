package sk.ab.common.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import sk.ab.common.entity.FirebasePlant;
import sk.ab.common.entity.PlantFilter;
import sk.ab.common.entity.PlantHeader;


/**
 * Firebase service
 */
public interface FirebaseService {

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("plants/{name}.json")
    Call<FirebasePlant> getDetail(@Path("name") String name);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("plants_v2/{taxonomyName}.json")
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
    @GET("plants_v2/{name}.json")
    Call<FirebasePlant> getPlant(@Path("name") String name);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("plants_to_update/list.json")
    Call<List<String>> getPlantToUpdate();

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("plants/{name}.json")
    Call<PlantFilter> getPlantFilter(@Path("name") String name);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("plants_headers/{id}.json")
    Call<PlantHeader> getPlantHeader(@Path("id") Integer id);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("plants_headers/{id}.json")
    Call<PlantHeader> savePlantHeader(@Path("id") String id, @Body PlantHeader plantHeader);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("counts_new.json")
    Call<Map> saveCount(@Body Map<String, Integer> count);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("lists_new.json")
    Call<Map> saveList(@Body Map<String, Map<String, Boolean>> list);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("lists_new.json")
    Call<Map> saveListIds(@Body Map<String, Map<Integer, Integer>> list);

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
    @PUT("search_new/{language}.json")
    Call<Object> saveSearch(@Path("language") String language, @Body Map<String, Map<String, Object>> plants);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("search_new/{language}/{key}.json")
    Call<Object> savePartialSearch(@Path("language") String language, @Path("key") String key, @Body Map<String, Boolean> plants);


    @PUT("APG III.json")
    Call<Object> saveAPGIII(@Body Object object);

    @PUT("APG III_new.json")
    Call<Object> saveAPGIIINew(@Body Object object);

    @GET("APG III_new.json")
    Call<Map<String, Object>> getAPGIIINew();

    @GET("APG IV.json")
    Call<Map<String, Object>> getAPGIV();

    @GET("APG IV_v2.json")
    Call<Map<String, Object>> getAPGIV2();

    @GET("APG IV_v3.json")
    Call<Map<String, Object>> getAPGIV3();

    @PUT("APG IV.json")
    Call<Object> saveAPGIV(@Body Object object);

    @PUT("translations.json")
    Call<Object> saveTranslations(@Body Object object);

    @PUT("search_photo_new.json")
    Call<Object> savePhotoSearch(@Body Object object);

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
    @GET("translations_taxonomy/{language}/{name}.json")
    Call<List<String>> getTranslationTaxonomy(@Path("language") String language, @Path("name") String name);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PUT("translations_taxonomy/{language}/{name}.json")
    Call<Object> saveTranslationTaxonomy(@Path("language") String language, @Path("name") String name, @Body Object object);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("translations_taxonomy/{language}.json")
    Call<Map<String, List<String>>> getTranslationTaxonomy(@Path("language") String language);

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

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @PATCH("translations_app.json")
    Call<Map> saveTranslationsApp(@Body Map<String, Map<String, Map<String, String>>> translationsApp);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("web.json")
    Call<Map<String, Map<String, String>>> getWebTranslations();

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @GET("search_v3/{language}.json")
    Call<Map<String, Object>> getSearch(@Path("language") String language);

    @PUT("plants_v2/{name}/id.json")
    Call<Integer> savePlantId(@Path("name") String name, @Body Integer id);

    @PUT("{path}{attribute}.json")
    Call<String> saveStringAttribute(@Path("path") String path, @Path("attribute") String attribute, @Body String value);
}
