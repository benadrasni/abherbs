package sk.ab.common.service;

import retrofit.Retrofit;

/**
 *
 * Created by adrian on 1.9.2015.
 */
public class FirebaseClient {
    public static final String API_URL = "https://abherbs-backend.firebaseio.com/";

    private FirebaseService firebaseService;

    public FirebaseClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        firebaseService = retrofit.create(FirebaseService.class);
    }

    public FirebaseService getApiService() {
        return firebaseService;
    }

}
