package sk.ab.common.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import sk.ab.common.entity.Count;
import sk.ab.common.entity.Plant;
import sk.ab.common.entity.PlantHeader;
import sk.ab.common.entity.PlantList;
import sk.ab.common.entity.Rate;
import sk.ab.common.entity.Taxon;
import sk.ab.common.entity.Taxonomy;
import sk.ab.common.entity.Translation;
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

    @GET("_ah/api/taxonomyApi/v1/plant/{plantName}/header")
    Call<PlantHeader> getHeader(@Path("plantName") String plantName);

    @POST("_ah/api/taxonomyApi/v1/plant/update/{plantName}")
    Call<Plant> update(@Path("plantName") String plantName,
                       @Query("attribute") String attribute,
                       @Query("values") String values,
                       @Query("operation") String operation,
                       @Query("type") String type);


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
    Call<Translation> saveTranslation(@Body Translation translation);

    @GET("_ah/api/translationApi/v1/translation/{key}")
    Call<Translation> getTranslation(@Path("key") String key);

    @GET("_ah/api/taxonomyApi/v1/find/{type}/{name}")
    Call<Taxon> getTaxon(@Path("type") String type,
                         @Path("name") String name);

    @GET("_ah/api/taxonomyApi/v1/find/{taxonLang}/{taxonName}/{taxonValue}")
    Call<Taxonomy> getTaxonomy(@Path("taxonLang") String taxonLang,
                               @Path("taxonName") String taxonName,
                               @Path("taxonValue") String taxonValue,
                               @Query("lang") String lang);

    @Headers({
            "Content-Type: application/json; charset: UTF-8"
    })
    @POST("_ah/api/rateApi/v1/rate")
    Call<Rate> saveRate(@Body Rate rateSave);

}
