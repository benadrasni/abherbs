package sk.ab.herbs.service;

import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import sk.ab.herbs.CountRequest;
import sk.ab.herbs.DetailRequest;
import sk.ab.herbs.ListRequest;

/**
 * Created by adrian on 1.9.2015.
 */
public interface HerbService {

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @POST("/rest/count")
    Call<Integer> getCount(@Body CountRequest countRequest);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @POST("/rest/list")
    Call<Map<Integer,Map<String,List<String>>>> getList(@Body ListRequest listRequest);

    @Headers({
            "Content-Type: application/json",
            "Accept-Charset: UTF-8",
            "charset: UTF-8"
    })
    @POST("/rest/detail")
    Call<Map<Integer,Map<String,List<String>>>> getDetail(@Body DetailRequest detailRequest);

}
