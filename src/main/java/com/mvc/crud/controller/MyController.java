package com.mvc.crud.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mvc.crud.model.Friend;
import com.mvc.crud.model.Movie;
import com.mvc.crud.model.Notification;
import com.mvc.crud.model.Recommendation;
import com.mvc.crud.repository.FriendRepository;
import com.mvc.crud.repository.NotificationRepository;
import com.mvc.crud.repository.RecommendationRepo;
import com.mvc.crud.repository.movierepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MyController {

    @Autowired
    movierepository movieRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    RecommendationRepo recommendationRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    // Home Feed
    @GetMapping("/")
    public String rootFeed(HttpServletRequest request, HttpSession session, Model model) {
        return prepareFeed(request, session, model);
    }

    @GetMapping("/feed")
    public String feedUrl(HttpServletRequest request, HttpSession session, Model model) {
        return prepareFeed(request, session, model);
    }

    private String prepareFeed(HttpServletRequest request, HttpSession session, Model model) {
        List<Movie> movies = movieRepository.findAllByOrderByIdDesc();
        model.addAttribute("movies", movies); // ✅ Always add movies

        Friend currentFriend = getCurrentFriendFromCookie(request);
        if (currentFriend == null) {
            return "feed-guest";
        }

        session.setAttribute("user", currentFriend);
        List<Friend> friends = friendRepository.findAll();
        List<Notification> notifications = notificationRepo.findByRecipientOrderByTimestampDesc(currentFriend);

        model.addAttribute("friends", friends);
        model.addAttribute("currentFriend", currentFriend);
        model.addAttribute("hasNotifications", !notifications.isEmpty());
        model.addAttribute("notifications", notifications);

        return "feed";
    }



    // Register
    @PostMapping("/quick-register")
    public String register(@RequestParam String name,
                           @RequestParam String mobile,
                           @RequestParam String password,
                           HttpSession session,
                           HttpServletResponse response) {

        Friend friend = friendRepository.findByMobile(mobile);
        if (friend == null) {
            friend = new Friend(name, mobile, password);
            friendRepository.save(friend);
        }

        session.setAttribute("user", friend);

        Cookie mobileCookie = new Cookie("userMobile", URLEncoder.encode(mobile, StandardCharsets.UTF_8));
        mobileCookie.setMaxAge(60 * 60 * 24 * 365);
        mobileCookie.setPath("/");
        response.addCookie(mobileCookie);

        return "redirect:/";
    }

    // Add Movie (GET)
    @GetMapping("/add-movie")
    public String showAddMovieForm(HttpServletRequest request, HttpSession session) {
        Friend friend = (Friend) session.getAttribute("user");
        if (friend == null) {
            friend = getCurrentFriendFromCookie(request);
            if (friend == null) return "redirect:/login";
            session.setAttribute("user", friend);
        }
        return "add-movie";
    }

    // Add Movie (POST)
    @PostMapping("/add-movie")
    public String addMovie(@ModelAttribute Movie movie, HttpServletRequest request, HttpSession session, Model model) {
        Friend currentUser = (Friend) session.getAttribute("user");

        if (currentUser == null) {
            currentUser = getCurrentFriendFromCookie(request);
            if (currentUser == null) return "redirect:/login";
            session.setAttribute("user", currentUser);
        }

        if (movieRepository.existsByNameIgnoreCase(movie.getName())) {
            model.addAttribute("error", "Movie with this name already exists!");
            return "add-movie";
        }

        movie.setOwner(currentUser);
        movieRepository.save(movie);
        return "redirect:/";
    }

    // Manage Movies
    @GetMapping("/manage")
    public String showUserMovies(HttpServletRequest request, Model model) {
        Friend friend = getCurrentFriendFromCookie(request);
        if (friend == null) return "redirect:/register";

        List<Movie> userMovies = movieRepository.findByOwnerId(friend.getId());
        model.addAttribute("movies", userMovies);
        return "manage-movies";
    }

    @PostMapping("/toggle-like")
    @ResponseBody
    public String toggleLike(@RequestParam int id, HttpSession session) {
        Friend currentUser = (Friend) session.getAttribute("user");
        if (currentUser == null) return "login-required";

        Optional<Movie> movieOpt = movieRepository.findById(id);
        if (movieOpt.isEmpty()) return "not-found";

        Movie movie = movieOpt.get();
        Friend freshUser = friendRepository.findById(currentUser.getId()).orElse(null);

        if (freshUser == null) return "user-not-found";

        List<Integer> likedMovies = freshUser.getLikedMovieIds();
        boolean alreadyLiked = likedMovies.contains(id);

        if (alreadyLiked) {
            likedMovies.remove(Integer.valueOf(id));
            if (movie.getLikes() > 0) movie.setLikes(movie.getLikes() - 1);
        } else {
            likedMovies.add(id);
            movie.setLikes(movie.getLikes() + 1);
        }

        // Save both movie and user
        friendRepository.save(freshUser);
        movieRepository.save(movie);

        return alreadyLiked ? "unliked" : "liked";
    }

    

    // Profile Page
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Friend user = (Friend) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Movie> addedMovies = movieRepository.findByOwner(user);
        List<Recommendation> receivedRecommendations = recommendationRepo.findByToFriend(user);

        model.addAttribute("user", user);
        model.addAttribute("addedMovies", addedMovies);
        model.addAttribute("receivedRecommendations", receivedRecommendations);

        return "profile";
    }

    // Recommend Movie
    @PostMapping("/recommend")
    public String recommendMovie(@RequestParam int movieId,
                                 @RequestParam int toFriendId,
                                 @RequestParam(required = false) String note,
                                 HttpSession session) {
        Friend fromFriend = (Friend) session.getAttribute("user");
        if (fromFriend == null) return "redirect:/login";

        Movie movie = movieRepository.findById(movieId).orElse(null);
        Friend toFriend = friendRepository.findById((long) toFriendId).orElse(null);

        if (movie != null && toFriend != null) {
            Recommendation rec = new Recommendation();
            rec.setMovie(movie);
            rec.setFromFriend(fromFriend);
            rec.setToFriend(toFriend); // <-- Use setToFriend (capital 'T')
            rec.setNote(note);
            recommendationRepo.save(rec);

            // Save notification for recipient
            String message = fromFriend.getName() + " recommended you the movie: " + movie.getName();
            Notification notification = new Notification(message, toFriend);
            notificationRepo.save(notification);
        }

        return "redirect:/";
    }

    @GetMapping("/delete-recommendation")
    public String deleteRecommendation(@RequestParam Integer id, HttpSession session) {
        Friend user = (Friend) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Recommendation rec = recommendationRepo.findById(id).orElse(null);
        if (rec != null && rec.getToFriend().getId().equals(user.getId())) {
            recommendationRepo.deleteById(id);
        }

        return "redirect:/profile";
    }





    // Delete Notification
    @PostMapping("/notification/delete")
    @ResponseBody
    public String deleteNotification(@RequestParam Long id) {
        notificationRepo.deleteById(id);
        return "deleted";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpServletResponse response, HttpSession session) {
        Cookie cookie = new Cookie("userMobile", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        session.invalidate();
        return "redirect:/";
    }
    @GetMapping("/delete-notification")
    public String deleteNotification1(@RequestParam("id") Long id) {
        notificationRepo.deleteById(id);
        return "redirect:/feed"; // Make sure you have a @GetMapping("/feed") controller
    }
    @GetMapping("/edit")
    public String editMovie(@RequestParam("id") int id, Model model) {
        Optional<Movie> movieOpt = movieRepository.findById(id);
        if (movieOpt.isPresent()) {
            model.addAttribute("m", movieOpt.get());
            return "edit"; // make sure the file is edit.html
        } else {
            return "redirect:/manage"; // fallback
        }
    }
    @GetMapping("/search-friends")
    public String showFriendSearchPage() {
        return "search-friends"; // this will show the blank form initially
    }

    @GetMapping("/search-friends/result")
    public String searchFriend(@RequestParam("query") String query, Model model) {
        List<Friend> results = friendRepository.findByNameContainingIgnoreCaseOrMobileContaining(query, query);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "search-friends";
    }
    @Transactional
    @PostMapping("/add-friend")
    public String addFriend(@RequestParam Long friendId, HttpSession session) {
        Friend currentUser = (Friend) session.getAttribute("user");
        if (currentUser == null) return "redirect:/login";

        // Re-fetch current user from DB with initialized friends list
        Friend freshCurrentUser = friendRepository.findById(currentUser.getId()).orElse(null);
        Friend friendToAdd = friendRepository.findById(friendId).orElse(null);

        if (freshCurrentUser != null && friendToAdd != null && !freshCurrentUser.getId().equals(friendToAdd.getId())) {
            freshCurrentUser.getFriends().add(friendToAdd);
            friendRepository.save(freshCurrentUser);
        }

        return "redirect:/search-friends";
    }


    // Get user from cookie
    private Friend getCurrentFriendFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("userMobile".equals(c.getName())) {
                    return friendRepository.findByMobile(c.getValue());
                }
            }
        }
        return null;
    }
    
}
