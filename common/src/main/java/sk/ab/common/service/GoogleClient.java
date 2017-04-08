package sk.ab.common.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * Created by adrian on 1.9.2015.
 */
public class GoogleClient {
    public static final String API_URL = "https://translation.googleapis.com";

    private GoogleService googleService;

    public GoogleClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        googleService = retrofit.create(GoogleService.class);
    }

    public GoogleService getApiService() {
        return googleService;
    }

}
