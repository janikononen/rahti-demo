package com.example.domain.user;

import com.example.domain.user.dto.UserRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("userForm", new UserRequest("", ""));
        return "users";
    }

    @PostMapping
    public String createUser(@ModelAttribute("userForm") UserRequest userRequest) {
        userService.create(userRequest);
        return "redirect:/users";
    }
}
