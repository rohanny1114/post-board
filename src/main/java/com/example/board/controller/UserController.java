package com.example.board.controller;

import com.example.board.dto.SigninInfo;
import com.example.board.dto.User;
import com.example.board.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.print.PrinterIOException;
import java.util.List;

/**
 * This class controls the user related actions.
 *
 * @author Rohan Kim
 */
@Controller
@RequiredArgsConstructor // Lombok: auto arg constructor creator for final params.
public class UserController {
    private final UserService userService;
    /**
     * Forward the join form template.
     *
     * @return join the page for a new user registration form
     */
    @GetMapping("/join")
    public String join() {
        return "join";
    }

    /**
     * Request server to save input user information to the database
     *
     * @param name is the name of the new user
     * @param email is the email of the new user to be used as id
     * @param password is the password of the new user
     * @return redirect:/welcome if registration success
     */
    @PostMapping("/userReg")
    public String userReg(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password
    ){
        // TEST INPUT VALUES
        System.out.println("name : " + name);
        System.out.println("email : " + email);
        System.out.println("password : " + password);
        userService.addUser(name, email, password);

        return "redirect:/welcome"; //  브라우저에게 자동으로 http://localhost:8080/welcome 으로 이동
    }

    /**
     *
     * @return welcome the page to show that the registration has successfully done
     */
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    /**
     * Sign in to the board
     *
     * @return sign in the page for signing in form
     */
    @GetMapping("/signin")
    public String signin() {
        return "signin";
    }

    /**
     * Check if the input user information exist in the database,
     * save the user information to the session if it matches with exiting data,
     * and return failure messages if it does match.
     *
     * @param email is the email of the exist user to be used as id
     * @param password is the password of the exist user
     * @return redirect to the home page if the input user information matched
     */
    @PostMapping("/userChk")
    public String userChk(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession httpSession // Auto manage session by Spring
    ){
        // TEST INPUT VALUES
        System.out.println("email : " + email);
        System.out.println("password : " + password);

        try {
            User user = userService.getUser(email);
            System.out.println("[SYSTEM] " + user);
            if (user.getPassword().equals(password)) {
                System.out.println("[SYSTEM] Correct Password");
                // Save input user information into Session
                SigninInfo signinInfo = new SigninInfo(user.getUserId(), user.getEmail(), user.getName());
                // Read roles and get roles on SigninInfo
                List<String> roles =  userService.getRoles(user.getUserId());
                signinInfo.setRoles(roles);

                httpSession.setAttribute("signinInfo", signinInfo);
                System.out.println("[SYSTEM] Signed user info set on the session");
            } else {
                throw new RuntimeException("[SYSTEM] Wrong Password");
            }
        } catch (Exception e) {
            return "redirect:/signin?error=true";
        }
        return "redirect:/";
    }

    /**
     * Sign out from the board and remove user information from the session
     *
     * @return redirect to home page
     */
    @GetMapping("/signout")
    public String signout(HttpSession session) {
        session.removeAttribute("signinInfo");
        return "redirect:/";
    }
}
