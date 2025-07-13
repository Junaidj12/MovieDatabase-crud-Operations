package com.mvc.crud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mvc.crud.model.Friend;
import com.mvc.crud.repository.FriendRepository;
import com.mvc.crud.repository.RecommendationRepo;
import com.mvc.crud.repository.movierepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class GuestController {

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private movierepository movierepository;

    @Autowired
    private RecommendationRepo recommendationRepo;

    // ✅ Login POST - uses mobile now, with admin logic
    @PostMapping("/login")
    public String login(@RequestParam String mobile,
                        @RequestParam String password,
                        HttpSession session,
                        HttpServletResponse response,
                        Model model) {

        // ✅ Admin Login Check
        if (mobile.equals("0987654321") && password.equals("Admin@1315")) {
            session.setAttribute("isAdmin", true);
            return "redirect:/admin";
        }

        // ✅ Normal User Login
        Friend user = friendRepository.findByMobileAndPassword(mobile, password);
        if (user == null) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }

        session.setAttribute("user", user);
        session.setAttribute("mobile", user.getMobile());

        // Optional: also set mobile cookie here if needed
        Cookie mobileCookie = new Cookie("userMobile", mobile);
        mobileCookie.setMaxAge(60 * 60 * 24 * 365);
        mobileCookie.setPath("/");
        response.addCookie(mobileCookie);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid mobile number or password.");
        }
        return "login";
    }

    // Register GET - show register form with prefilled mobile if provided
    @GetMapping("/register")
    public String registerPage(@RequestParam(required = false) String mobile, Model model) {
        model.addAttribute("mobile", mobile);
        return "register";
    }
}
