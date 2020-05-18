package com.netcracker.service;

import com.vaadin.flow.server.VaadinService;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements Serializable {

    public String isValid(String name){
        if (name == null){
            return "Can't be empty";
        }

        Pattern namePattern = Pattern.compile("^[A-Za-zа-яА-Я]+(([',. -][A-Za-zа-яА-Я ])?[A-Za-zа-яА-Я]*)*$");
        Matcher matcher = namePattern.matcher(name);

        if(!matcher.matches()) return "Invalid symbols";

        return null;
    }

    public String validateUsername(String username) {

        if (username == null) {
            return "Username can't be empty";
        }
        if (username.length() < 4) {
            return "Username can't be shorter than 4 characters";
        }
        List<String> reservedNames = Arrays.asList("admin", "test", "null", "void");
        if (reservedNames.contains(username)) {
            return String.format("'%s' is not available as a username", username);
        }
        return null;
    }

    public String getCookieByName(String name) {
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }
        return null;
    }


    public static class ServiceException extends Exception {
        public ServiceException(String msg) {
            super(msg);
        }
    }


}
