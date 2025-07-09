package com.mvc.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mvc.crud.model.movie;

public interface movierepository extends JpaRepository<movie, Integer>{

}
