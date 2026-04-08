package com.example.firebaseandroid.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.firebaseandroid.data.model.Movie;
import com.example.firebaseandroid.data.repository.MovieRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MovieRepository repository;

    private final MutableLiveData<List<Movie>> searchResults = new MutableLiveData<>();

    public HomeViewModel() {
        repository = MovieRepository.getInstance();
    }

    public LiveData<List<Movie>> getMovies() {
        return repository.getMovies();
    }

    public LiveData<List<Movie>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsLoading() {
        return repository.getIsLoading();
    }

    public LiveData<String> getError() {
        return repository.getError();
    }

    public void loadMovies() {
        repository.loadNowShowingMovies();
    }

    public void searchMovies(String query) {
        repository.searchMovies(query);
        repository.getSearchResults().observeForever(results -> {
            if (results != null) {
                searchResults.setValue(results);
            }
        });
    }
}
