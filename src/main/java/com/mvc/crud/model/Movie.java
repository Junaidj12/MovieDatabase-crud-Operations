package com.mvc.crud.model;

import jakarta.persistence.*;

@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    int id;
    @Column(nullable = false, unique = true)
    String name;
    String plot;
    String imageLink;
    String genre;

    @ManyToOne
    private Friend owner;
    @Column
    private int likes = 0;
    
    @Transient
    private boolean likedByCurrentUser;

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }



    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPlot() { return plot; }
    public void setPlot(String plot) { this.plot = plot; }

    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Friend getOwner() { return owner; }
    public void setOwner(Friend owner) { this.owner = owner; }
	public int getLikes() {
		return likes;
	}
	public void setLikes(int likes) {
		this.likes = likes;
	}
    
}
