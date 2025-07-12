package com.mvc.crud.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String mobile;

    private String password;

    // Movies added by the user
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Movie> addedMovies = new ArrayList<>();

    // Recommendations received by this user
    @OneToMany(mappedBy = "toFriend", cascade = CascadeType.ALL)
    private List<Recommendation> receivedRecommendations = new ArrayList<>();

    // Liked movie IDs
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> likedMovieIds = new ArrayList<>();

    // Friends list
    @ManyToMany
    @JoinTable(
        name = "user_friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Friend> friends = new ArrayList<>();

    // Constructors
    public Friend() {}

    public Friend(String name, String mobile, String password) {
        this.name = name;
        this.mobile = mobile;
        this.password = password;
    }

    // Getters
    public Long getId() { return id; }

    public String getName() { return name; }

    public String getMobile() { return mobile; }

    public String getPassword() { return password; }

    public List<Movie> getAddedMovies() { return addedMovies; }

    public List<Recommendation> getReceivedRecommendations() { return receivedRecommendations; }

    public List<Integer> getLikedMovieIds() { return likedMovieIds; }

    public List<Friend> getFriends() { return friends; }

    // Setters
    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setMobile(String mobile) { this.mobile = mobile; }

    public void setPassword(String password) { this.password = password; }

    public void setAddedMovies(List<Movie> addedMovies) { this.addedMovies = addedMovies; }

    public void setReceivedRecommendations(List<Recommendation> receivedRecommendations) {
        this.receivedRecommendations = receivedRecommendations;
    }

    public void setLikedMovieIds(List<Integer> likedMovieIds) {
        this.likedMovieIds = likedMovieIds;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }
    public int getTotalLikes() {
        if (addedMovies == null) return 0;
        return addedMovies.stream().mapToInt(Movie::getLikes).sum();
    }

}
