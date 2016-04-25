package sk.ab.herbs.service;

import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import sk.ab.herbs.TranslationSaveRequest;

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
    Call<TranslationSaveRequest> saveTranslation(@Body TranslationSaveRequest translationSaveRequest);

    @GET("_ah/api/translationApi/v1/translation/{key}")
    Call<TranslationSaveRequest> getTranslation(@Path("key") String key);
}
