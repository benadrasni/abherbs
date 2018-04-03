package sk.ab.herbsbase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 *
 * Created by adrian on 1/3/2018.
 */

public class BaseFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "BaseFIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        ((BaseApp)getApplication()).setToken(refreshedToken);
    }
}
