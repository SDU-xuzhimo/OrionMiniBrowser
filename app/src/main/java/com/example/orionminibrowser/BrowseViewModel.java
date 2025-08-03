package com.example.orionminibrowser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class BrowseViewModel extends ViewModel {

    WebRepository repository;

    public BrowseViewModel(){
        repository=new WebRepository();
    }



    public void handleProgressChanged(int newProgress){
        repository.updateProgress(newProgress);
        if(newProgress==100)
            repository.isLoading.postValue(false);
    }

    public void handleTitleReceived(String title){
        repository.updateTitle(title);
    }

    public void handlePageStarted(String url){
        repository.updateUrl(url);
        repository.isLoading.postValue(true);
        repository.hasError.postValue(false);
    }

    public void handlePageFinished(){
        repository.isLoading.postValue(false);
        repository.hasError.postValue(false);
    }

    public void handleError(){
        repository.isLoading.postValue(false);
        repository.hasError.postValue(true);
    }


    public LiveData<Integer> getProgress(){
        return repository.progress;
    }

    public LiveData<String> getPageTitle(){
        return repository.pageTitle;
    }

    public LiveData<Boolean> getIsLoading(){

        return repository.isLoading;
    }

    public LiveData<Boolean> getHasError(){
        return repository.hasError;
    }

    public LiveData<String> getUrl(){
        return repository.currentUrl;
    }
}

