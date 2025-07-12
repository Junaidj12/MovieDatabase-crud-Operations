package com.mvc.crud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.mvc.crud.model.Friend;
import com.mvc.crud.model.Recommendation;

public interface RecommendationRepo extends JpaRepository<Recommendation, Integer> {
    List<Recommendation> findByFromFriend(Friend fromFriend);
    List<Recommendation> findByToFriend(Friend friend);
    @Transactional
    void deleteAllByMovieId(int movieId);

}
