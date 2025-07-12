package com.mvc.crud.model;

import jakarta.persistence.*;

@Entity
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToOne
    private Friend fromFriend;

    @ManyToOne
    private Friend toFriend;

    private String note;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Friend getFromFriend() {
        return fromFriend;
    }

    public void setFromFriend(Friend fromFriend) {
        this.fromFriend = fromFriend;
    }

    public Friend getToFriend() {
        return toFriend;
    }

    public void setToFriend(Friend toFriend) {
        this.toFriend = toFriend;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
