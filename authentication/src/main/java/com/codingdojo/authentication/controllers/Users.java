package com.codingdojo.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codingdojo.authentication.models.User;
import com.codingdojo.authentication.services.UserService;
import com.codingdojo.authentication.validator.UserValidator;

// imports removed for brevity
@Controller
public class Users {
	private final UserValidator userValidator;
    private final UserService userService;
    
    public Users(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }
    
    @RequestMapping("/registration")
    public String registerForm(@ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
    @RequestMapping("/login")
    public String login() {
        return "loginPage.jsp";
    }

    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
    	userValidator.validate(user, result);
        // if result has errors, return the registration page (don't worry about validations just now)
    	if(result.hasErrors()) {
    		return "registrationPage.jsp";
    	}
    	userService.registerUser(user);
    	session.setAttribute("userId",user.getId());
    	return "redirect:/home";
    	// else, save the user in the database, save the user id in session, and redirect them to the /home route
    }
//    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
        // if the user is authenticated, save their user id in session
    	if(userService.authenticateUser(email, password)) {
    		User user = userService.findByEmail(email);
    		session.setAttribute("userId", user.getId());
    		return "redirect:/home";
    	}
    	model.addAttribute("error", "Invalid login credentials");
    	return "loginPage.jsp";
        // else, add error messages and return the login page
    }
//    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
    	if(session.getAttribute("userId")==null) {
    		return "redirect:/registration";
    	}
    	Long id = (Long) session.getAttribute("userId");
    	User loggedInUser = userService.findUserById(id);
    	model.addAttribute("user",loggedInUser);
    	return "homePage.jsp";
        // get user from session, save them in the model and return the home page
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session
    	session.invalidate();
        // redirect to login page
    	return "redirect:/login";
    }
}
    
    