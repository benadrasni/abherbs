/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.ab.herbs.billingmodule;

import com.android.billingclient.api.Purchase;

import java.util.List;

import sk.ab.herbs.billingmodule.billing.BillingManager.BillingUpdatesListener;
import sk.ab.herbs.billingmodule.skulist.row.MonthlyDelegate;
import sk.ab.herbs.billingmodule.skulist.row.YearlyDelegate;

/**
 * Handles control logic of the BasePlayActivity
 */
public class MainViewController {
    private static final String TAG = "MainViewController";

    private final UpdateListener mUpdateListener;
    private BasePlayActivity mActivity;

    // Tracks if we currently own subscriptions SKUs
    private boolean mMonthly;
    private boolean mYearly;

    public MainViewController(BasePlayActivity activity) {
        mUpdateListener = new UpdateListener();
        mActivity = activity;
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public boolean isMonthlySubscribed() {
        return mMonthly;
    }

    public boolean isYearlySubscribed() {
        return mYearly;
    }

    /**
     * Handler to billing updates
     */
    private class UpdateListener implements BillingUpdatesListener {
        @Override
        public void onBillingClientSetupFinished() {
            mActivity.onBillingManagerSetupFinished();
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchaseList) {
            mMonthly = false;
            mYearly = false;

            for (Purchase purchase : purchaseList) {
                switch (purchase.getSku()) {
                    case MonthlyDelegate.SKU_ID:
                        mMonthly = true;
                        break;
                    case YearlyDelegate.SKU_ID:
                        mYearly = true;
                        break;
                }
            }

            mActivity.showRefreshedUi();
        }
    }
}