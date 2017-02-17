package sk.ab.common.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * Created by adrian on 1.9.2015.
 */
public class HerbCloudClient {
    public static final String API_URL = "https://abherbs-backend.appspot.com";

    private HerbCloudService herbCloudService;

    public HerbCloudClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        herbCloudService = retrofit.create(HerbCloudService.class);
    }

    public HerbCloudService getApiService() {
        return herbCloudService;
    }

}
