package com.mvc.crud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mvc.crud.model.Friend;
import com.mvc.crud.model.Movie;

public interface movierepository extends JpaRepository<Movie, Integer>{

	List<Movie> findAllByOrderByIdDesc();

	List<Movie> findByOwner(Friend currentFriend);

	List<Movie> findByOwnerId(Long long1);
	
	boolean existsByNameIgnoreCase(String name);

}
