package sk.ab.herbs.billingmodule;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.billingmodule.R;

import sk.ab.herbs.billingmodule.billing.BillingManager;
import sk.ab.herbs.billingmodule.billing.BillingProvider;
import sk.ab.herbs.billingmodule.skulist.AcquireFragment;

import static com.android.billingclient.api.BillingClient.BillingResponse;
import static sk.ab.herbs.billingmodule.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED;

/**
 *
 */
public abstract class BasePlayActivity extends AppCompatActivity implements BillingProvider {
    // Debug tag, for logging
    private static final String TAG = "BasePlayActivity";

    // Tag for a dialog that allows us to find it when screen was rotated
    private static final String DIALOG_TAG = "dialog";

    private BillingManager mBillingManager;
    private AcquireFragment mAcquireFragment;
    private MainViewController mViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());

        // Start the controller and load game data
        mViewController = new MainViewController(this);

        // Try to restore dialog fragment if we were showing it prior to screen rotation
        if (savedInstanceState != null) {
            mAcquireFragment = (AcquireFragment) getSupportFragmentManager()
                    .findFragmentByTag(DIALOG_TAG);
        }

        // Create and initialize BillingManager which talks to BillingLibrary
        mBillingManager = new BillingManager(this, mViewController.getUpdateListener());

        Button purchaseButton = findViewById(R.id.button_purchase);

        if (purchaseButton != null) {
            purchaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPurchaseButtonClicked(view);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager != null
                && mBillingManager.getBillingClientResponseCode() == BillingResponse.OK) {
            mBillingManager.queryPurchases();
        }
    }

    @Override
    public BillingManager getBillingManager() {
        return mBillingManager;
    }

    @Override
    public boolean isMonthlySubscribed() {
        return mViewController.isMonthlySubscribed();
    }

    @Override
    public boolean isYearlySubscribed() {
        return mViewController.isYearlySubscribed();
    }

    protected abstract int getLayoutResId();

    /**
     * User clicked the "Buy Gas" button - show a purchase dialog with all available SKUs
     */
    public void onPurchaseButtonClicked(final View arg0) {
        Log.d(TAG, "Purchase button clicked.");

        if (mAcquireFragment == null) {
            mAcquireFragment = new AcquireFragment();
        }

        if (!isAcquireFragmentShown()) {
            mAcquireFragment.show(getSupportFragmentManager(), DIALOG_TAG);

            if (mBillingManager != null
                    && mBillingManager.getBillingClientResponseCode()
                            > BILLING_MANAGER_NOT_INITIALIZED) {
                mAcquireFragment.onManagerReady(this);
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroying helper.");
        if (mBillingManager != null) {
            mBillingManager.destroy();
        }
        super.onDestroy();
    }

    /**
     * Remove loading spinner and refresh the UI
     */
    public void showRefreshedUi() {
        if (mAcquireFragment != null) {
            mAcquireFragment.refreshUI();
        }
    }

    /**
     * Show an alert dialog to the user
     * @param messageId String id to display inside the alert dialog
     * @param optionalParam Optional attribute for the string
     */
    @UiThread
    void alert(@StringRes int messageId, @Nullable Object optionalParam) {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new RuntimeException("Dialog could be shown only from the main thread");
        }

        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setNeutralButton("OK", null);

        if (optionalParam == null) {
            bld.setMessage(messageId);
        } else {
            bld.setMessage(getResources().getString(messageId, optionalParam));
        }

        bld.create().show();
    }

    void onBillingManagerSetupFinished() {
        if (mAcquireFragment != null) {
            mAcquireFragment.onManagerReady(this);
        }
    }

    public boolean isAcquireFragmentShown() {
        return mAcquireFragment != null && mAcquireFragment.isVisible();
    }
}
