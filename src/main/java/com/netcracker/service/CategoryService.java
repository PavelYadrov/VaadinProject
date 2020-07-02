package com.netcracker.service;


import com.vaadin.flow.component.accordion.AccordionPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private FeignUserService feignUserService;
    private UserService userService;

    private AccordionPanel currentAccordion;

    @Autowired
    public CategoryService(FeignUserService feignUserService, UserService userService) {
        this.feignUserService = feignUserService;
        this.userService = userService;
    }

}
