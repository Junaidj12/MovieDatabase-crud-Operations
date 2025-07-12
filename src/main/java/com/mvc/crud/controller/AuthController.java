package com.mvc.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mvc.crud.model.Friend;
import com.mvc.crud.repository.FriendRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

	@Autowired 
	FriendRepository friendRepository;
	

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String mobile, HttpSession session) {
        Friend friend = friendRepository.findByMobile(mobile);

        if (friend == null) {
            friend = new Friend();
            friend.setName(name);
            friend.setMobile(mobile);
            friendRepository.save(friend);
        }

        session.setAttribute("currentFriend", friend);
        return "redirect:/feed"; // or index.html
    }
}
