package sk.ab.common.service;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by adrian on 1.9.2015.
 */
public interface GoogleService {

    @GET("/language/translate/v2")
    Call<Map<String, Map<String, List<Map<String, String>>>>> translate(@Query("key") String key,
                                                                        @Query("source") String source,
                                                                        @Query("target") String target,
                                                                        @Query("q") List<String> qlist);


}
