package com.mls;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;


public class MainActivity extends Activity {
    private WebView webView;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        setContentView(webView);
        
        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE);
        
        setupWebView();
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void startGame() {
            Intent intent = new Intent(MainActivity.this, LevelsActivity.class);
            startActivity(intent);
        }
        
        @JavascriptInterface
        public int getHighScore() {
            return prefs.getInt("high_score", 0);
        }
        
        @JavascriptInterface
        public int getCompletedLevels() {
            int maxUnlocked = prefs.getInt("max_unlocked_level", 1);
            int completed = 0;
            for (int i = 1; i <= 12; i++) {
                if (prefs.getBoolean("level_completed_" + i, false)) {
                    completed++;
                }
            }
            return completed;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // تحديث الإحصائيات عند العودة للشاشة الرئيسية
        webView.reload();
    }
}