package com.mvc.crud.controller;

import com.mvc.crud.model.Friend;
import com.mvc.crud.model.Movie;
import com.mvc.crud.model.Notification;
import com.mvc.crud.repository.FriendRepository;
import com.mvc.crud.repository.NotificationRepository;
import com.mvc.crud.repository.RecommendationRepo;
import com.mvc.crud.repository.movierepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FriendRepository friendRepo;

    @Autowired
    private movierepository movieRepo;

    @Autowired
    private NotificationRepository notificationRepo;
    
    @Autowired
    private RecommendationRepo recommendationRepo;

    // ✅ Admin Dashboard
    @GetMapping
    public String adminRedirect(HttpSession session) {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login?error=unauthorized";

        long totalUsers = friendRepo.count();
        long totalMovies = movieRepo.count();
        List<Friend> allUsers = friendRepo.findAll();
        List<Movie> allMovies = movieRepo.findAll();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalMovies", totalMovies);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("allMovies", allMovies);
        return "admin"; // Admin dashboard HTML (admin.html)
    }

    // ✅ Ban User by Mobile
    @PostMapping("/ban-user")
    public String banUser(@RequestParam String mobile, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login?error=unauthorized";

        Friend user = friendRepo.findByMobile(mobile);
        if (user != null) {
            friendRepo.delete(user);
        }
        return "redirect:/admin/dashboard";
    }

    // ✅ Broadcast Notification
    @PostMapping("/broadcast")
    public String broadcast(@RequestParam String message, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login?error=unauthorized";

        List<Friend> allUsers = friendRepo.findAll();
        for (Friend user : allUsers) {
            Notification n = new Notification(message, user);
            notificationRepo.save(n);
        }
        return "redirect:/admin/dashboard";
    }

    // ✅ Delete Movie by ID
    @PostMapping("/delete-movie")
    public String deleteMovie(@RequestParam int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login?error=unauthorized";

        // ✅ Step 1: Delete associated recommendations first
        recommendationRepo.deleteAllByMovieId(id);

        // ✅ Step 2: Now delete the movie
        movieRepo.deleteById(id);

        return "redirect:/admin/dashboard";
    }


    // ✅ Logout Admin
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("isAdmin");
        return "redirect:/login";
    }

    // ✅ Helper to check admin login
    private boolean isAdmin(HttpSession session) {
        Object isAdmin = session.getAttribute("isAdmin");
        return isAdmin != null && (Boolean) isAdmin;
    }
}
