package com.mvc.crud.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mvc.crud.model.*;
import com.mvc.crud.repository.*;

import jakarta.servlet.http.*;

@Controller
public class MyController {

    @Autowired
    movierepository movieRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    RecommendationRepo recommendationRepo;

    @Autowired
    NotificationRepository notificationRepo;

    @Autowired
    private Cloudinary cloudinary;

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
        model.addAttribute("movies", movies);

        Friend currentFriend = getCurrentFriendFromCookie(request);
        if (currentFriend == null) {
            return "feed-guest";
        }

        session.setAttribute("user", currentFriend);
        model.addAttribute("friends", friendRepository.findAll());
        List<Notification> notifications = notificationRepo.findByRecipientOrderByTimestampDesc(currentFriend);
        model.addAttribute("currentFriend", currentFriend);
        model.addAttribute("hasNotifications", !notifications.isEmpty());
        model.addAttribute("notifications", notifications);

        return "feed";
    }

    // Quick Register
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

    // Add Movie - GET
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

    // Add Movie - POST
    @Transactional
    @PostMapping("/add-movie")
    public String addMovie(@RequestParam("name") String name,
                           @RequestParam("genre") String genre,
                           @RequestParam("plot") String plot,
                           @RequestParam("image") MultipartFile image,
                           HttpSession session,
                           Model model) {
        Friend user = (Friend) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        try {
            Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("secure_url");

            Movie movie = new Movie(name, genre, plot, imageUrl);
            movie.setOwner(user);
            movieRepository.save(movie);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Image upload failed. Please try again.");
            return "add-movie";
        }

        return "redirect:/";
    }

    // Manage Movies
    @GetMapping("/manage")
    public String showUserMovies(HttpServletRequest request, Model model) {
        Friend friend = getCurrentFriendFromCookie(request);
        if (friend == null) return "redirect:/register";

        List<Movie> userMovies = movieRepository.findByOwnerIdOrderByIdDesc(friend.getId());
        model.addAttribute("movies", userMovies);
        return "manage-movies";
    }

    // Profile
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Friend user = (Friend) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        List<Movie> addedMovies = movieRepository.findByOwnerIdOrderByIdDesc(user.getId());
        Collections.reverse(addedMovies);
        List<Recommendation> receivedRecommendations = recommendationRepo.findByToFriend(user);

        model.addAttribute("user", user);
        model.addAttribute("addedMovies", addedMovies);
        model.addAttribute("receivedRecommendations", receivedRecommendations);

        return "profile";
    }

    // Like Toggle
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

        friendRepository.save(freshUser);
        movieRepository.save(movie);

        return alreadyLiked ? "unliked" : "liked";
    }

    // Recommendation
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
            rec.setToFriend(toFriend);
            rec.setNote(note);
            recommendationRepo.save(rec);

            Notification notification = new Notification(fromFriend.getName() + " recommended you the movie: " + movie.getName(), toFriend);
            notificationRepo.save(notification);
        }

        return "redirect:/";
    }

    // Delete Recommendation
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

    @GetMapping("/delete-notification")
    public String deleteNotification1(@RequestParam("id") Long id) {
        notificationRepo.deleteById(id);
        return "redirect:/feed";
    }

    // Edit Movie (GET)
    @GetMapping("/edit")
    public String editMovie(@RequestParam("id") int id, Model model) {
        Optional<Movie> movieOpt = movieRepository.findById(id);
        if (movieOpt.isPresent()) {
            model.addAttribute("m", movieOpt.get());
            return "edit";
        } else {
            return "redirect:/manage";
        }
    }

    // Update Movie (with optional image change)
    @PostMapping("/update")
    public String updateMovie(@ModelAttribute Movie m,
                              @RequestParam("image") MultipartFile image,
                              HttpSession session,
                              Model model) {
        Friend user = (Friend) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        Optional<Movie> existingOpt = movieRepository.findById(m.getId());
        if (existingOpt.isEmpty()) return "redirect:/manage";

        Movie existing = existingOpt.get();

        try {
            if (!image.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                existing.setImageLink((String) uploadResult.get("secure_url"));
            }

            existing.setName(m.getName());
            existing.setGenre(m.getGenre());
            existing.setPlot(m.getPlot());

            movieRepository.save(existing);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Image update failed.");
            return "edit";
        }

        return "redirect:/manage";
    }

    // Friend Search
    @GetMapping("/search-friends")
    public String showFriendSearchPage() {
        return "search-friends";
    }

    @GetMapping("/search-friends/result")
    public String searchFriend(@RequestParam("query") String query, Model model) {
        List<Friend> results = friendRepository.findByNameContainingIgnoreCaseOrMobileContaining(query, query);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "search-friends";
    }

    // Helper
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
