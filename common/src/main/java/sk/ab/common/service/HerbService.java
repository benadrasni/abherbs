package sk.ab.common.service;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import sk.ab.common.entity.request.DetailRequest;

/**
 * Created by adrian on 1.9.2015.
 */
public interface HerbService {

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @POST("/rest/detail")
    Call<Map<Integer,Map<String,List<String>>>> getDetail(@Body DetailRequest detailRequest);
}
