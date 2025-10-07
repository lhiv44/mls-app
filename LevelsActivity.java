package com.mls;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
import android.util.Log;

public class LevelsActivity extends Activity {
    private WebView webView;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        setContentView(webView);
        
        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE);
        
        setupWebView();
        webView.loadUrl("file:///android_asset/levels.html");
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
        public void startLevel(int level) {
            // التحقق إذا كان المستوى مفتوحاً
            int maxUnlocked = prefs.getInt("max_unlocked_level", 1);
            Log.d("LevelsActivity", "🔍 المستوى المطلوب: " + level + " - أقصى مستوى مفتوح: " + maxUnlocked);
            
            // طباعة حالة جميع المستويات للتأكد
            for (int i = 1; i <= 3; i++) {
                boolean completed = prefs.getBoolean("level_completed_" + i, false);
                Log.d("LevelsActivity", "📊 المستوى " + i + " مكتمل: " + completed);
            }
            
            if (level <= maxUnlocked) {
                Intent intent = new Intent(LevelsActivity.this, GameActivity.class);
                intent.putExtra("level", level);
                startActivity(intent);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LevelsActivity.this, 
                            "المستوى " + level + " مقفل! أكمل المستوى " + (level-1) + " أولاً", 
                            Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        
        @JavascriptInterface
        public int getMaxUnlockedLevel() {
            int maxUnlocked = prefs.getInt("max_unlocked_level", 1);
            Log.d("LevelsActivity", "📁 إرجاع أقصى مستوى مفتوح: " + maxUnlocked);
            return maxUnlocked;
        }
        
        @JavascriptInterface
        public void goBack() {
            finish();
        }
        
        @JavascriptInterface
        public boolean isLevelCompleted(int level) {
            boolean completed = prefs.getBoolean("level_completed_" + level, false);
            Log.d("LevelsActivity", "✅ المستوى " + level + " مكتمل: " + completed);
            return completed;
        }
        
        @JavascriptInterface
        public void resetGame() {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LevelsActivity.this, "تم إعادة تعيين اللعبة", Toast.LENGTH_SHORT).show();
                }
            });
            
            // إعادة تحميل الصفحة بعد إعادة التعيين
            webView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.reload();
                }
            }, 1000);
        }
        
        @JavascriptInterface
        public String getDebugInfo() {
            int maxUnlocked = prefs.getInt("max_unlocked_level", 1);
            StringBuilder info = new StringBuilder();
            for (int i = 1; i <= 3; i++) {
                boolean completed = prefs.getBoolean("level_completed_" + i, false);
                info.append("المستوى ").append(i).append(": ").append(completed ? "مكتمل" : "غير مكتمل").append(" | ");
            }
            info.append("أقصى مفتوح: ").append(maxUnlocked);
            return info.toString();
        }
        
        @JavascriptInterface
        public void forceUnlockLevel(int level) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("max_unlocked_level", level);
            for (int i = 1; i < level; i++) {
                editor.putBoolean("level_completed_" + i, true);
            }
            editor.apply();
            
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LevelsActivity.this, "تم فتح المستوى " + level, Toast.LENGTH_SHORT).show();
                }
            });
            
            webView.reload();
        }
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // إعادة تحميل الصفحة عند العودة لتحديث حالة المستويات
        Log.d("LevelsActivity", "🔄 إعادة تحميل صفحة المستويات");
        webView.reload();
    }
}