package com.example.orionminibrowser;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URI;
import java.net.URL;
import java.util.Stack;

public class BrowsePage extends AppCompatActivity {

    //Stack<String>  historyStack=new Stack<>();
    private String startUri;
    private WebView mWebView;

    private WebSettings mWebSettings;

    private TextView webTitle;
    private ImageButton Back;
    private ImageButton Share;



    private ProgressBar progressBar;
    private boolean isPageLoaded;

     private boolean isSuccessLoaded;

    class mWebViewClient extends WebViewClient{

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (request.getUrl().toString().contains("file:///android_asset/error_page.html")) {
                return true; // 禁止加载
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            //view.stopLoading();
            if (!failingUrl.startsWith("file:///android_asset/error_page.html")) {
                view.loadUrl("file:///android_asset/error_page.html");
            }
            isSuccessLoaded=false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view,url,favicon);
            isPageLoaded = false;
            progressBar.setProgress(0);
            isSuccessLoaded=true;
            startUri = url;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //progressBar.setVisibility(INVISIBLE);

//            if(!startUri.equals(url)){
//                mWebView.clearHistory();
//                historyStack.pop();
//            }

//            if (historyStack.isEmpty() || !historyStack.peek().equals(url)) {
//                historyStack.push(url);
//            }


            Back.setVisibility(VISIBLE);
            if(isSuccessLoaded)
                Share.setVisibility(VISIBLE);
            else
                Share.setVisibility(GONE);
            webTitle.setVisibility(VISIBLE);

            Back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId()==R.id.back){
                        onBackPressed();
                    }
                }
            });

            Share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId()==R.id.share)
                    {
                        Toast.makeText(BrowsePage.this,"share "+mWebView.getUrl(),Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }


    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (mWebView.getUrl() != null && mWebView.getUrl().contains("file:///android_asset/error_page.html")) {
            if (mWebView.canGoBack()) {
                // 回退到错误页之前的页面（原始请求页）
                mWebView.goBack();
                // 若原始页仍失败，需额外处理（见方案 3）
            } else {
                super.onBackPressed(); // 无历史记录则退出 Activity
            }
        } else {
            super.onBackPressed();
        }
    }

    class mWebChromeClient extends WebChromeClient{

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);

                // 页面开始加载时显示进度条
                if (newProgress < 100 && progressBar.getVisibility() != VISIBLE) {
                    progressBar.setVisibility(VISIBLE);
                    isPageLoaded = false;
                }

                // 更新进度值
                progressBar.setProgress(newProgress);

                // 加载完成后隐藏进度条（有延迟让用户看到100%）
                if (newProgress == 100) {
                    isPageLoaded = true;
                    // 延迟隐藏进度条（500ms后）
                    new Handler().postDelayed(() -> {
                        if (isPageLoaded) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }, 500);
                }

        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

            webTitle.setText(title);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_page);


        Intent intent = getIntent();
        String webSite = intent.getStringExtra("URL");


        mWebView = (WebView) findViewById(R.id.web_view);
        webTitle = (TextView) findViewById(R.id.web_title);
        Back = (ImageButton) findViewById(R.id.back);
        Share = (ImageButton) findViewById(R.id.share);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        mWebSettings = mWebView.getSettings();
        mWebView.setWebViewClient(new mWebViewClient());
        mWebView.setWebChromeClient(new mWebChromeClient());


        websetting();



        mWebView.loadUrl(webSite);
    }

    private void websetting(){
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setAllowFileAccessFromFileURLs(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }
    }

    @Override
    protected void onDestroy() {
        if(mWebView!=null){
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebSettings=null;
        }
        super.onDestroy();
    }
}