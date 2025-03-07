package io.muzoo.ssc.project.backend.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @GetMapping("/test")
    public String test() {
        return "If this show, login success.";
    }

    @PostMapping("/api/login")
    public String login(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            request.login(username, password);
            return "Login Successful";
        } catch (ServletException e) {
            e.printStackTrace();
            return "Fail to login";
        }
    }

    @GetMapping("/api/logout")
    public String logout(HttpServletRequest request) {
        try {
            request.logout();
            return "Logout Successful";
        } catch (ServletException e) {
            e.printStackTrace();
            return "Fail to logout";
        }
    }

}
