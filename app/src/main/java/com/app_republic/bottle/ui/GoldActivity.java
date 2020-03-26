package com.app_republic.bottle.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.app_republic.bottle.R;
import com.app_republic.bottle.data.SharedPreferenceHelper;
import com.app_republic.bottle.data.StaticConfig;
import com.app_republic.bottle.model.GoldItem;
import com.app_republic.bottle.model.User;
import com.app_republic.bottle.service.ByGoldInterface;
import com.app_republic.bottle.service.UpdateInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GoldActivity extends AppCompatActivity implements RewardedVideoAdListener, ByGoldInterface, BillingProcessor.IBillingHandler {

    private TextView balance;
    private Button watch;
    private int gold = -1;
    private User user;
    private RewardedVideoAd mRewardedVideoAd;
    private String admob_id;
    BillingProcessor bp;
    private Button buy;
    ArrayList<GoldItem> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gold_layout);

        user = SharedPreferenceHelper.getInstance(this).getUserInfo();

        admob_id = SharedPreferenceHelper.getInstance(GoldActivity.this).getAdmobId();

        balance = findViewById(R.id.balance);
        watch = findViewById(R.id.watch);

        buy = findViewById(R.id.buy);
        buy.setEnabled(false);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        FirebaseDatabase.getInstance().getReference().child("user")
                .child(StaticConfig.UID).child("gold")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    gold = Integer.parseInt(dataSnapshot.getValue().toString());
                    balance.setText(gold + " coins");
                } else {
                    gold = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRewardedVideoAd.loadAd(admob_id,
                        new AdRequest.Builder().build());
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyGoldDialog dialog = new buyGoldDialog();
                Bundle args = new Bundle();
                args.putParcelableArrayList("items", items);
                dialog.setArguments(args);
                if (!isDestroyed() && !isFinishing())
                    getSupportFragmentManager().beginTransaction().add(dialog, "buy_gold").commitAllowingStateLoss();
            }
        });

        bp = new BillingProcessor(this, getString(R.string.console_license_key), this);
        bp.initialize();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        int j = 0;
        if (gold != -1) {
            gold = gold + rewardItem.getAmount();
            FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID).child("gold").setValue(gold).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        balance.setText(gold + " coins");
                    }
                }
            });
        }

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }


    @Override
    public void buy(final int coins, String price) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Buy " + coins + " coins for " + price);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 bp.purchase(GoldActivity.this,"coins_"+coins);
            }
        });
        builder.show();

    }

    @Override
    public void onBackPressed() {
        mRewardedVideoAd.destroy(this);
        finish();
    }

    @Override
    public void onBillingInitialized() {
        ArrayList<String> list = new ArrayList<>();
        list.add("coins_5");
        list.add("coins_20");
        list.add("coins_50");
        list.add("coins_100");
        list.add("coins_500");

        items = new ArrayList<>();
        items.add(new GoldItem(bp.getPurchaseListingDetails(list.get(0)).priceText, 5, 0));
        items.add(new GoldItem(bp.getPurchaseListingDetails(list.get(1)).priceText, 20, 25));
        items.add(new GoldItem(bp.getPurchaseListingDetails(list.get(2)).priceText, 50, 30));
        items.add(new GoldItem(bp.getPurchaseListingDetails(list.get(3)).priceText, 100, 35));
        items.add(new GoldItem(bp.getPurchaseListingDetails(list.get(4)).priceText, 500, 50));

        buy.setEnabled(true);

    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        final int coins = Integer.parseInt(details.purchaseInfo.purchaseData.productId.split("_")[1]);

        FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                .child("gold")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int number = Integer.parseInt(dataSnapshot.getValue().toString());
                        gold = number + coins;
                        FirebaseDatabase.getInstance().getReference().child("user")
                                .child(StaticConfig.UID).child("gold")
                                .setValue(gold).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    balance.setText(gold + " coins");
                                    AlertDialog.Builder builder = new AlertDialog.Builder(GoldActivity.this);
                                    builder.setPositiveButton(R.string.ok, null);
                                    builder.setMessage(getString(R.string.success_buy_gold, coins));
                                    builder.show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        /*FirebaseDatabase.getInstance().getReference().child("user").child(StaticConfig.UID)
                .child("gold").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                int value = 0;
                if (mutableData.getValue() != null)
                    value = Integer.parseInt(mutableData.getValue().toString());

                mutableData.setValue(value + coins);
                gold = value + coins;
                balance.setText(gold + " coins");

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GoldActivity.this);
                builder.setPositiveButton(R.string.ok, null);
                builder.setMessage(getString(R.string.success_buy_gold, coins));
                builder.show();
            }
        });*/

        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (bp != null) {
            bp.release();
        }

    }

}
