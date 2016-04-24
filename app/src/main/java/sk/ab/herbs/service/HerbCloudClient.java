package sk.ab.herbs.service;

import retrofit.Retrofit;

/**
 * Created by adrian on 1.9.2015.
 */
public class HerbCloudClient {
    public static final String API_URL = "http://10.0.2.2:8080";

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
