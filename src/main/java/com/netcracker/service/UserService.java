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

        Pattern namePattern = Pattern.compile("^[A-Za-zа-яА-Я.'-]+$");
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
    public Cookie getCookie(String name){
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    public String validateText(String text){
        if (text == null) {
            return "Can't be empty";
        }

        if (text.trim().length() < 5) return "Can't be less than 5 symbols";


        Pattern namePattern = Pattern.compile("^[A-Za-zа-яА-Я-0-9 !():,.'-]+$");
        Matcher matcher = namePattern.matcher(text);

        if (!matcher.matches()) return "Invalid symbols";

        return null;
    }

    public String validatePrice(String price) {
        if (price == null) {
            return "Can't be empty";
        }
        if ((price.startsWith("0") && price.charAt(1) != '.') || (price.startsWith("0") && price.length() < 2)) {
            return "Invalid number";
        }

        Pattern namePattern = Pattern.compile("^(\\d+\\.\\d+)$|^(\\d+)$");
        Matcher matcher = namePattern.matcher(price);
        if (!matcher.matches()) return "Invalid number";

        if (price.length() > 30) {
            return "price must be less than 30 symbols ";
        }

        return null;
    }

}
