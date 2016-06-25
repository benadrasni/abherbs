package sk.ab.common.service;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import sk.ab.common.entity.Count;
import sk.ab.common.entity.Plant;
import sk.ab.common.entity.PlantList;
import sk.ab.common.entity.RateSave;
import sk.ab.common.entity.Taxonomy;
import sk.ab.common.entity.TranslationSave;
import sk.ab.common.entity.request.ListRequest;

/**
 * Datastore service
 */
public interface HerbCloudService {

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @POST("_ah/api/taxonomyApi/v1/plant/count")
    Call<Count> getCount(@Body ListRequest listRequest);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @POST("_ah/api/taxonomyApi/v1/plant/list")
    Call<PlantList> getList(@Body ListRequest listRequest);

    @GET("_ah/api/taxonomyApi/v1/plant/{plantName}")
    Call<Plant> getDetail(@Path("plantName") String plantName);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @POST("_ah/api/taxonomyApi/v1/plant/{taxonomyName}")
    Call<Plant> synchronizePlant(@Path("taxonomyName") String taxonomyName,
                                 @Query("taxonomyWiki") String taxonomyWiki,
                                 @Body Plant plant);


    @Headers({
            "Content-Type: application/json; charset: UTF-8"
    })
    @POST("_ah/api/translationApi/v1/translation")
    Call<TranslationSave> saveTranslation(@Body TranslationSave translationSave);

    @GET("_ah/api/translationApi/v1/translation/{key}")
    Call<TranslationSave> getTranslation(@Path("key") String key);

    @GET("_ah/api/taxonomyApi/v1/find/{taxonLang}/{taxonName}/{taxonValue}")
    Call<Taxonomy> getTaxonomy(@Path("taxonLang") String taxonLang,
                               @Path("taxonName") String taxonName,
                               @Path("taxonValue") String taxonValue,
                               @Query("lang") String lang);

    @Headers({
            "Content-Type: application/json; charset: UTF-8"
    })
    @POST("_ah/api/rateApi/v1/rate")
    Call<RateSave> saveRate(@Body RateSave rateSave);

}
