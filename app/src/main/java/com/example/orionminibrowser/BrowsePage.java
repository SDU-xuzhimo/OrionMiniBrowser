package com.example.orionminibrowser;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.View.inflate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
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
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.orionminibrowser.databinding.ActivityBrowsePageBinding;

import java.net.URI;
import java.net.URL;
import java.util.Stack;

public class BrowsePage extends AppCompatActivity {

    private ActivityBrowsePageBinding binding;
    private BrowseViewModel viewModel;

    private CustomWebChromeClient customWebChromeClient;

    private CustomWebViewClient customWebViewClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(BrowsePage.this,R.layout.activity_browse_page);
        viewModel = new ViewModelProvider(this).get(BrowseViewModel.class);



        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);


        binding.webView.setWebViewClient(new CustomWebViewClient(viewModel));
        binding.webView.setWebChromeClient(new CustomWebChromeClient(viewModel));


        webSetting();


        Intent intent = getIntent();
        String webSite = intent.getStringExtra("URL");

        binding.webView.loadUrl(webSite);

        setupObservers();
    }




    public void webSetting() {

        WebSettings mWebSettings = binding.webView.getSettings();
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


    public void setupObservers(){

        viewModel.getIsLoading().observe(this,isLoad->{
            if(isLoad)
                binding.progressBar.setVisibility(VISIBLE);
            else
                binding.progressBar.setVisibility(GONE);
        });

        viewModel.getPageTitle().observe(this,title->{
            if(title!=null)
                binding.webTitle.setText(title);
        });

        viewModel.getProgress().observe(this,progress->{

            binding.progressBar.setProgress(progress);
        });

        viewModel.getHasError().observe(this,hasError->{

            if(hasError){
                binding.webView.stopLoading();
                binding.webView.loadUrl("file:///android_asset/error_page.html");

            }
        });


        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.share){
                    Toast.makeText(BrowsePage.this,viewModel.getUrl().getValue(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.back)
                    onBackPressed();
            }
        });

    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {

        if(binding.webView.canGoBack())
            binding.webView.goBack();
        else
            super.onBackPressed();

    }
}


//class ActivityBrowseBinding extends ViewDataBinding{
//
//    private ProgressBar progressBar;
//    private WebView webView;
//    private ImageButton backButton;
//    private ImageButton shareButton;
//    private TextView title;
//
//    public ImageButton getBackButton() {
//        return backButton;
//    }
//
//    public void setBackButton(ImageButton backButton) {
//        this.backButton = backButton;
//    }
//
//    public ProgressBar getProgressBar() {
//        return progressBar;
//    }
//
//    public void setProgressBar(ProgressBar progressBar) {
//        this.progressBar = progressBar;
//    }
//
//    public WebView getWebView() {
//        return webView;
//    }
//
//    public void setWebView(WebView webView) {
//        this.webView = webView;
//    }
//
//    public ImageButton getShareButton() {
//        return shareButton;
//    }
//
//    public void setShareButton(ImageButton shareButton) {
//        this.shareButton = shareButton;
//    }
//
//    public TextView getTitle() {
//        return title;
//    }
//
//    public void setTitle(TextView title) {
//        this.title = title;
//    }
//}


class CustomWebViewClient extends WebViewClient{

    private BrowseViewModel viewModel;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return false;
    }

    public CustomWebViewClient(BrowseViewModel viewModel){
        super();
        this.viewModel=viewModel;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        viewModel.handlePageStarted(url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        if(viewModel.getHasError().equals(false)) {
            super.onPageFinished(view, url);
            viewModel.handlePageFinished();
        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        //super.onReceivedError(view, request, error);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(TAG, "Error details: " +
                    "Code=" + error.getErrorCode() +
                    ", Desc=" + error.getDescription() +
                    ", ForMainFrame=" + request.isForMainFrame());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (error.getErrorCode() == WebViewClient.ERROR_UNKNOWN &&
                    error.getDescription().toString().contains("ORB")) {

                Log.w(TAG, "ORB blocked resource: " + request.getUrl());

                // 对于非主框架错误，可以选择忽略
                if (!request.isForMainFrame()) {
                    Log.d(TAG, "Ignoring ORB error for subresource");
                    return;
                }
            }
        }
        if(error.getErrorCode()==ERROR_CONNECT&&request.isForMainFrame())
           viewModel.handleError();

    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }
}

class CustomWebChromeClient extends WebChromeClient{
    private BrowseViewModel viewModel;

    public CustomWebChromeClient(BrowseViewModel viewModel){
        super();
        this.viewModel=viewModel;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);

        viewModel.handleTitleReceived(title);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        viewModel.handleProgressChanged(newProgress);
    }
}


class WebRepository  {

    MutableLiveData<Integer> progress = new MutableLiveData<>();
    MutableLiveData<String> pageTitle = new MutableLiveData<>();
    MutableLiveData<String> currentUrl=new MutableLiveData<>();
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> hasError=new MutableLiveData<>();


    public WebRepository(){
        progress.postValue(0);
        hasError.postValue(false);
        isLoading.postValue(false);

    }
    public void updateProgress(Integer newProgress){

        progress.postValue(newProgress);
        if(newProgress==100){
            isLoading.postValue(true);
        }
    }

    public void updateTitle(String title){
        pageTitle.postValue(title);
    }

    public void updateUrl(String url){
        currentUrl.postValue(url);
    }

    public void handleError(){
        hasError.postValue(true);
        isLoading.postValue(false);
    }


}