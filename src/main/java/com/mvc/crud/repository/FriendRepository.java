package com.mvc.crud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mvc.crud.model.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    // ✅ Use "mobile" instead of "number"
    Friend findByMobile(String mobile);

    Friend findByMobileAndPassword(String mobile, String password);
    
    List<Friend> findByNameContainingIgnoreCaseOrMobileContaining(String name, String mobile);

}
