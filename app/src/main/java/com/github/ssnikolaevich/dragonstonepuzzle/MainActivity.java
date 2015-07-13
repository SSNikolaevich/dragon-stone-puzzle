package com.github.ssnikolaevich.dragonstonepuzzle;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        initAds();
    }

    private void init() {

    }

    private void initAds() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void onRate(View view) {
        rateApp();
    }

    protected void rateApp() {
        try
        {
            startActivity(
                new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + getPackageName())
                )
            );
        } catch (ActivityNotFoundException e) {
            String message = getResources().getString(R.string.no_play_store_installed);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onShare(View view) {
        shareApp();
    }

    protected void shareApp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String msgSubject = getString(R.string.share_msg_subject);
        String msgText = getString(R.string.share_msg_text);
        String shareCaption = getString(R.string.share_via);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, msgSubject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, msgText);
        startActivity(Intent.createChooser(sharingIntent, shareCaption));
    }

    public void onStart(View view) {
        Intent intent = new Intent(this, LevelSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
