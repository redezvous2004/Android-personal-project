package com.example.firebaseandroid.data.model;

import java.util.List;

public class Movie {
    private String movieId;
    private String title;
    private String description;
    private String posterUrl;
    private String backdropUrl;
    private String genre;
    private int durationMinutes;
    private double rating;
    private String director;
    private List<String> cast;
    private String language;
    private String rated; // G, PG, PG-13, R, etc.
    private String releaseDate;
    private boolean nowShowing;
    private long createdAt;

    public Movie() {}

    public Movie(String movieId, String title, String genre, int durationMinutes, String posterUrl) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.posterUrl = posterUrl;
        this.nowShowing = true;
        this.createdAt = System.currentTimeMillis();
        this.rating = 0.0;
    }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getBackdropUrl() { return backdropUrl; }
    public void setBackdropUrl(String backdropUrl) { this.backdropUrl = backdropUrl; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public List<String> getCast() { return cast; }
    public void setCast(List<String> cast) { this.cast = cast; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getRated() { return rated; }
    public void setRated(String rated) { this.rated = rated; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public boolean isNowShowing() { return nowShowing; }
    public void setNowShowing(boolean nowShowing) { this.nowShowing = nowShowing; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}