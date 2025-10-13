package com.mls;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

public class GameActivity extends Activity {
    private WebView webView;
    private SharedPreferences prefs;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        setContentView(webView);
        
        currentLevel = getIntent().getIntExtra("level", 1);
        prefs = getSharedPreferences("game_prefs", MODE_PRIVATE);
        
        setupWebView();
        webView.loadUrl("file:///android_asset/game.html?level=" + currentLevel);
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        
        webView.setWebViewClient(new WebViewClient());
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void goBack() {
            finish();
        }
        
        @JavascriptInterface
        public int getCurrentLevel() {
            return currentLevel;
        }
        
        @JavascriptInterface
        public int getCurrentScore() {
            return prefs.getInt("current_score", 100);
        }
        
        @JavascriptInterface
        public int getCorrectAnswers() {
            return prefs.getInt("correct_answers_level_" + currentLevel, 0);
        }
        
        @JavascriptInterface
        public String getUsedQuestions() {
            return prefs.getString("used_questions_level_" + currentLevel, "");
        }
        
        @JavascriptInterface
        public void saveProgress(int correctAnswers, String usedQuestions) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("correct_answers_level_" + currentLevel, correctAnswers);
            editor.putString("used_questions_level_" + currentLevel, usedQuestions);
            editor.putInt("last_level_played", currentLevel);
            editor.apply();
            
            Log.d("GameActivity", "تم حفظ التقدم - المستوى: " + currentLevel + 
                  "، الإجابات الصحيحة: " + correctAnswers);
        }
        
        @JavascriptInterface
        public String[] getQuestions() {
            try {
                Resources res = getResources();
                String packageName = getPackageName();
                
                int questionsId = res.getIdentifier(
                    "level" + currentLevel + "_questions", 
                    "array", 
                    packageName
                );
                
                if (questionsId != 0) {
                    return res.getStringArray(questionsId);
                } else {
                    return getDefaultQuestions();
                }
            } catch (Exception e) {
                return getDefaultQuestions();
            }
        }
        
        @JavascriptInterface
        public String[] getAnswers() {
            try {
                Resources res = getResources();
                String packageName = getPackageName();
                
                int answersId = res.getIdentifier(
                    "level" + currentLevel + "_answers", 
                    "array", 
                    packageName
                );
                
                if (answersId != 0) {
                    return res.getStringArray(answersId);
                } else {
                    return getDefaultAnswers();
                }
            } catch (Exception e) {
                return getDefaultAnswers();
            }
        }
        
        private String[] getDefaultQuestions() {
            switch (currentLevel) {
                case 1:
                    return new String[]{
                        "من هو أول نبي في الإسلام؟",
                        "من هو النبي الذي سمي بخليل الله؟",
                        "من هو النبي الذي بنى السفينة؟",
                        "من هو النبي الذي لقب بكليم الله؟",
                        "من هو النبي الذي ألقى في النار فجعلها الله برداً وسلاماً؟",
                        "من هو النبي الذي سمي بأبي الأنبياء؟",
                        "من هو النبي الذي فلق البحر؟",
                        "من هو النبي الذي عاش في بطن الحوت؟",
                        "من هو النبي الذي كان يصنع من الطين طيراً؟",
                        "من هو خاتم الأنبياء والمرسلين؟"
                    };
                case 2:
                    return new String[]{
                        "ما هي أول سورة في القرآن؟",
                        "ما هي أطول سورة في القرآن؟",
                        "ما هي أقصر سورة في القرآن؟",
                        "ما هي السورة التي تسمى قلب القرآن؟",
                        "ما هي السورة التي تسمى عروس القرآن؟",
                        "ما هي السورة التي تبدأ بالحمد لله؟",
                        "ما هي السورة التي لم تبدأ بالبسملة؟",
                        "ما هي السورة التي ذكرت فيها البسملة مرتين؟",
                        "ما هي السورة التي تسمى سورة النبي؟",
                        "ما هي السورة التي تسمى المنجية؟"
                    };
                default:
                    return new String[]{
                        "سؤال افتراضي 1 للمستوى " + currentLevel,
                        "سؤال افتراضي 2 للمستوى " + currentLevel,
                        "سؤال افتراضي 3 للمستوى " + currentLevel,
                        "سؤال افتراضي 4 للمستوى " + currentLevel,
                        "سؤال افتراضي 5 للمستوى " + currentLevel,
                        "سؤال افتراضي 6 للمستوى " + currentLevel,
                        "سؤال افتراضي 7 للمستوى " + currentLevel,
                        "سؤال افتراضي 8 للمستوى " + currentLevel,
                        "سؤال افتراضي 9 للمستوى " + currentLevel,
                        "سؤال افتراضي 10 للمستوى " + currentLevel
                    };
            }
        }
        
        private String[] getDefaultAnswers() {
            switch (currentLevel) {
                case 1:
                    return new String[]{
                        "آدم", "إبراهيم", "نوح", "موسى", "إبراهيم", 
                        "إبراهيم", "موسى", "يونس", "عيسى", "محمد"
                    };
                case 2:
                    return new String[]{
                        "الفاتحة", "البقرة", "الكوثر", "يس", "الرحمن", 
                        "الفاتحة", "التوبة", "النمل", "التحريم", "الملك"
                    };
                default:
                    return new String[]{
                        "جواب1", "جواب2", "جواب3", "جواب4", "جواب5",
                        "جواب6", "جواب7", "جواب8", "جواب9", "جواب10"
                    };
            }
        }
        
        @JavascriptInterface
        public void updateScore(int score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("current_score", score);
            
            int highScore = prefs.getInt("high_score", 0);
            if (score > highScore) {
                editor.putInt("high_score", score);
            }
            
            editor.apply();
        }
        
        @JavascriptInterface
        public void completeLevel() {
            SharedPreferences.Editor editor = prefs.edit();
            
            // وضع علامة أن المستوى الحالي مكتمل
            editor.putBoolean("level_completed_" + currentLevel, true);
            
            // الحصول على أقصى مستوى مفتوح حالياً
            int maxUnlocked = prefs.getInt("max_unlocked_level", 1);
            Log.d("GameActivity", "المستوى الحالي: " + currentLevel + " - أقصى مستوى مفتوح: " + maxUnlocked);
            
            // إذا كان المستوى الحالي يساوي أقصى مستوى مفتوح، فافتح المستوى التالي
            if (currentLevel == maxUnlocked) {
                int nextLevel = currentLevel + 1;
                if (nextLevel <= 12) {
                    editor.putInt("max_unlocked_level", nextLevel);
                    Log.d("GameActivity", "✅ تم فتح المستوى: " + nextLevel);
                    
                    // تنظيف التقدم القديم للمستوى الجديد
                    editor.remove("correct_answers_level_" + nextLevel);
                    editor.remove("used_questions_level_" + nextLevel);
                }
            }
            
            // تنظيف التقدم الحالي بعد الإكمال
            editor.remove("correct_answers_level_" + currentLevel);
            editor.remove("used_questions_level_" + currentLevel);
            
            editor.apply();
            
            // طباعة للتأكد من الحفظ
            int newMaxUnlocked = prefs.getInt("max_unlocked_level", 1);
            Log.d("GameActivity", "🎯 بعد الحفظ - أقصى مستوى مفتوح: " + newMaxUnlocked);
        }
        
        @JavascriptInterface
        public void goToLevels() {
            Intent intent = new Intent(GameActivity.this, LevelsActivity.class);
            startActivity(intent);
            finish();
        }
        
        @JavascriptInterface
        public String debugInfo() {
            int maxUnlocked = prefs.getInt("max_unlocked_level", 1);
            boolean level1Completed = prefs.getBoolean("level_completed_1", false);
            boolean level2Completed = prefs.getBoolean("level_completed_2", false);
            
            return "المستوى 1 مكتمل: " + level1Completed + 
                   " | المستوى 2 مكتمل: " + level2Completed +
                   " | أقصى مستوى مفتوح: " + maxUnlocked;
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
}