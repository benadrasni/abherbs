package sk.ab.herbs.service;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import sk.ab.herbs.PlantTaxonomy;
import sk.ab.herbs.TranslationSave;

/**
 * Created by adrian on 1.9.2015.
 */
public interface HerbCloudService {

    @POST("_ah/api/myApi/v1/sayHi/{name}")
    Call<Map<String, String>> sayHi(@Path("name") String name);

    @Headers({
            "Content-Type: application/json; charset: UTF-8"
    })
    @POST("_ah/api/translationApi/v1/translation")
    Call<TranslationSave> saveTranslation(@Body TranslationSave translationSave);

    @GET("_ah/api/translationApi/v1/translation/{key}")
    Call<TranslationSave> getTranslation(@Path("key") String key);

    @GET("_ah/api/taxonomyApi/v1/find/{taxonName}/{taxonValue}")
    Call<PlantTaxonomy> getTaxonomy(@Path("taxonName") String taxonName, @Path("taxonValue") String taxonValue,
                                    @Query("lang") String lang);
}
