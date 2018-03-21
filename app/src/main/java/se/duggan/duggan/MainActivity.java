package se.duggan.duggan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String dugganHttp = "http://www.duggan.se";
    private static final String dugganHttps = "https://www.duggan.se";
    private static final String[] dugganUrls = { dugganHttp, dugganHttps };

    boolean isFirstPageStart = true;
    private ProgressBar spinner;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.progressBar1);

        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView.loadUrl(dugganHttp);
    }

    //Back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    // This allows for a splash screen
    // (and hide elements once the page loads)
    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            // only make it invisible the FIRST time the app is run
            if (isFirstPageStart) {
                webview.setVisibility(webview.INVISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            isFirstPageStart = false;
            spinner.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // If this is a duggan page url, just view it in the webview
            if(StringUtils.startsWithAny(url, dugganUrls)) {
                return false;
            }

            // Otherwise let it be handled by appropriate app or by the standard browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

            if (!activities.isEmpty()) {
                view.getContext().startActivity(intent);
            }
            return true;
        }

    }
}