package sk.ab.common.service;

import retrofit.Retrofit;

/**
 *
 * Created by adrian on 1.9.2015.
 */
public class GoogleClient {
    public static final String API_URL = "https://www.googleapis.com";

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
