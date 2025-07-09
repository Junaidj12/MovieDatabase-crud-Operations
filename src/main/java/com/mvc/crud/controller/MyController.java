package com.mvc.crud.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mvc.crud.model.movie;
import com.mvc.crud.repository.movierepository;

@Controller
public class MyController {
	
	@Autowired
	movierepository repository;
	
	@GetMapping("/")
	public String local() {
		return "index.html";
	}
	
	@GetMapping("/add")
	public String next() {
		return "add_movie.html";
	}
	
	@PostMapping("/add")
	public String add(@ModelAttribute movie movie, ModelMap map) {
		map.put("message", "Movie added sucessfully !");
		repository.save(movie);
		return "index.html";
	}
	
	@GetMapping("/delete")
	public String delete(@RequestParam int id, ModelMap map) {
		repository.deleteById(id);
		map.put("delete", "Movie sucessfully deleted");
		return "index.html";
	}
	
	@GetMapping("/view")
	public String view(ModelMap map) {
		List<movie> list = repository.findAll();
		map.put("list", list);
		return "fetch.html";
	}
	
	@GetMapping("/edit")
	public String edit(@RequestParam int id, ModelMap map) {
	    map.addAttribute("m", repository.findById(id).orElse(null));
	    map.put("delete", "movie updated sucessfully!");
	    return "edit";
	}
	@PostMapping("/update")
	public String updateMovie(@ModelAttribute movie movie, ModelMap map) {
	    repository.save(movie);
	    map.put("message", "Movie updated successfully!");
	    return "index"; 
	}




	
	
}
