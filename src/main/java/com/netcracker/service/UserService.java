package com.netcracker.service;

import com.netcracker.components.ChatEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.VaadinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.servlet.http.Cookie;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService implements Serializable {

    @Autowired
    private Flux<ChatEvent> messages;

    @Autowired
    private UnicastProcessor<ChatEvent> publisher;

    public String isValid(String name) {
        if (name == null) {
            return "Can't be empty";
        }
        if (name.length() < 2 || name.length() > 13) {
            return "Length must be between 2 and 13 characters";
        }
        Pattern namePattern = Pattern.compile("^[A-Za-zа-яА-Я.'-]+$");
        Matcher matcher = namePattern.matcher(name);
        if (!matcher.matches()) return "Invalid symbols";
        return null;
    }

    public String validateUsername(String username) {

        if (username == null) {
            return "Username can't be empty";
        }
        if (username.length() < 4 || username.length() > 20) {
            return "Username must be between 4 and 20 characters";
        }
        List<String> reservedNames = Arrays.asList("admin", "test", "null", "void", "user");
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

    public String validateText(String text, Boolean title) {
        if (text == null || StringUtils.isEmpty(text)) {
            return "Can't be empty";
        }
        if (text.trim().length() < 5) return "Can't be less than 5 symbols";
        if (title) {
            if (text.trim().length() > 19) return "Can't be large than 19 symbols";
        }
        Pattern namePattern = Pattern.compile("^[A-Za-zа-яА-Я-0-9 !():,.'-]+$");
        Matcher matcher = namePattern.matcher(text);
        if (!matcher.matches()) return "Invalid symbols";
        return null;
    }

    public String validatePrice(String price) {
        if (price == null) {
            return "Can't be empty";
        }
        if ((price.startsWith("0") && price.length() < 2) || (price.startsWith("0") && price.charAt(1) != '.')) {
            return "Invalid number";
        }
        Pattern namePattern = Pattern.compile("^(\\d+\\.\\d+)$|^(\\d+)$");
        Matcher matcher = namePattern.matcher(price);
        if (!matcher.matches()) return "Invalid number";
        if (price.length() > 10) {
            return "price must be less than 10 symbols ";
        }
        return null;
    }

    public boolean checkParameter(String param) {
        Pattern namePattern = Pattern.compile("^(\\d+)$");
        Matcher matcher = namePattern.matcher(param);
        if (!matcher.matches()) return false;
        return true;
    }

    public String setParam(BeforeEvent beforeEvent, String id) {
        Location location = beforeEvent.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();
        Map<String, List<String>> parametersMap = queryParameters.getParameters();
        List<String> params = parametersMap.get("id");
        if (params == null || !checkParameter(params.get(0))) {
            UI.getCurrent().getPage().setLocation("mainPage");
            return null;
        } else return params.get(0);
    }

    public void setParam(String page, String param) {
        List<String> param1 = new ArrayList<>();
        Map<String, List<String>> parametersMap = new HashMap<String, List<String>>();
        param1.add(param);

        parametersMap.put("id", param1);

        QueryParameters qp = new QueryParameters(parametersMap);
        UI.getCurrent().navigate(page, qp);
    }

    public Flux<ChatEvent> getMessages() {
        return messages;
    }

    public UnicastProcessor<ChatEvent> getPublisher() {
        return publisher;
    }
}
