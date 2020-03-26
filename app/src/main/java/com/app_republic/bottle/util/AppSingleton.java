package com.app_republic.bottle.util;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.app_republic.bottle.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AppSingleton {
    private static AppSingleton mAppSingletonInstance;
    private AppCompatActivity mContext;
    public String ADMOB_NATIVE_UNIT_ID = "";
    public String ADMOB_BANNER_UNIT_ID = "";
    public String ADMOB_INTER_UNIT_ID = "";

    private FirebaseStorage storage;

    private Gson gson;
    private Picasso picasso;
    private AdLoader adLoader;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private InterstitialAd mInterstitialAd;
    private DatabaseReference databaseReference;


    private AppSingleton(Context context) {
        mContext = (AppCompatActivity) context;
        gson = new Gson();
    }


    public static synchronized AppSingleton getInstance(Context context) {
        if (mAppSingletonInstance == null) {
            mAppSingletonInstance = new AppSingleton(context);
        }
        return mAppSingletonInstance;
    }


    public Picasso getPicasso() {
        if (picasso == null)
            picasso = Picasso.get();
        return picasso;

    }


    public FirebaseFirestore getDb() {
        if (db == null)
            db = FirebaseFirestore.getInstance();
        return db;
    }

    public DatabaseReference getDatabaseReference() {
        if (databaseReference == null)
            databaseReference = FirebaseDatabase.getInstance().getReference();
        return databaseReference;
    }

    public InterstitialAd getInterstitialAd() {

        if (mInterstitialAd == null) {
            mInterstitialAd = new InterstitialAd(mContext);
            mInterstitialAd.setAdUnitId(mAppSingletonInstance.ADMOB_INTER_UNIT_ID);
        }
        return mInterstitialAd;
    }

    public FirebaseStorage getFirebaseStorage() {
        if (storage == null)
            storage = FirebaseStorage.getInstance();
        return storage;
    }

    public FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null)
            firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth;
    }

    public Gson getGson() {
        return gson;
    }


    private void insertAdsInMenuItems(RecyclerView.Adapter adapter,
                                      List<UnifiedNativeAd> mNativeAds,
                                      List contentList,
                                      List completeList) {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (contentList.size() / mNativeAds.size()) + 1;
        int index = 0;
        for (UnifiedNativeAd ad : mNativeAds) {
            completeList.add(index, ad);
            index = index + offset;
        }


        adapter.notifyDataSetChanged();
    }

    public void loadNativeAds(List<UnifiedNativeAd> mNativeAds,
                              RecyclerView recyclerView,
                              RecyclerView.Adapter adapter,
                              List contentList,
                              List completeList,
                              int number_of_ads

    ) {

        if (mNativeAds.isEmpty()) {
            AdLoader.Builder builder = new AdLoader.Builder(mContext, mAppSingletonInstance.ADMOB_NATIVE_UNIT_ID);

            adLoader = builder.forUnifiedNativeAd(unifiedNativeAd -> {
                mNativeAds.add(unifiedNativeAd);
                if (!adLoader.isLoading()) {
                    insertAdsInMenuItems(adapter, mNativeAds, contentList, completeList);
                }
            }).withAdListener(
                    new AdListener() {
                        @Override
                        public void onAdFailedToLoad(int errorCode) {
                            if (!adLoader.isLoading()) {
                                insertAdsInMenuItems(adapter, mNativeAds, contentList, completeList);
                            }
                        }
                    }).build();

            // Load the Native Express ad.
            adLoader.loadAds(new AdRequest.Builder().build(), number_of_ads);
        } else
            insertAdsInMenuItems(adapter, mNativeAds, contentList, completeList);





    }


    public void loadNativeAd(FrameLayout frameLayout) {



        AdLoader loader = new AdLoader.Builder(mContext, ADMOB_NATIVE_UNIT_ID)
                .forUnifiedNativeAd(unifiedNativeAd -> {

                    View unifiedNativeLayoutView = LayoutInflater.from(
                            mContext).inflate(R.layout.ad_unified, frameLayout, false);
                    UnifiedNativeAdViewHolder holder = new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);

                    UnifiedNativeAdViewHolder.populateNativeAdView(unifiedNativeAd,
                            holder.getAdView());

                    frameLayout.removeAllViews();
                    frameLayout.addView(unifiedNativeLayoutView);

                })

                .build();

        loader.loadAd(new AdRequest.Builder().build());
    }

    public void loadNativeAdBig(FrameLayout frameLayout) {

        AdLoader loader = new AdLoader.Builder(mContext, ADMOB_NATIVE_UNIT_ID)
                .forUnifiedNativeAd(unifiedNativeAd -> {

                    View unifiedNativeLayoutView = LayoutInflater.from(
                            mContext).inflate(R.layout.ad_unified, frameLayout, false);
                    UnifiedNativeAdViewHolder holder = new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);

                    UnifiedNativeAdViewHolder.populateNativeAdView(unifiedNativeAd,
                            holder.getAdView());

                    frameLayout.removeAllViews();
                    frameLayout.addView(unifiedNativeLayoutView);

                })

                .build();

        loader.loadAd(new AdRequest.Builder().build());


    }


}
